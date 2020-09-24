package core.router;

import core.router.handler.ExceptionHandler;
import core.router.handler.HttpRouteHandler;
import core.router.request.RequestContext;
import core.router.response.HttpHandler;
import core.router.response.Response;
import core.router.response.ResponseBody;
import core.serde.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class HttpRouter implements RequestHandler<ByteBuf, ByteBuf> {

    private static final HttpRouteHandler NOT_FOUND_MAPPING_HANDLER =
            requestContext -> Response.builder()
                    .body("Route not found")
                    .contentType(ContentType.JSON)
                    .status(HttpResponseStatus.NOT_FOUND)
                    .build();

    private static final ExceptionHandler DEFAULT_EXCEPTION_HANDLER =
            throwable -> Response.builder()
                    .body(throwable.getMessage())
                    .contentType(ContentType.JSON)
                    .status(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                    .build();

    private Set<HttpHandler> httpHandlers = new HashSet<>();

    private Map<Class<? extends Throwable>, ExceptionHandler> errorResponses = new HashMap<>();

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        Response handlingResult = handleRequest(request, response);

        Observable<ResponseBody> responseBody = getResponseBody(handlingResult);

        return responseBody.flatMap(body -> {
            if (body.isError()) {

                log.error("Error occurred!", body.getError());

                Response errorResponse = errorResponses
                        .getOrDefault(body.getError().getClass(), DEFAULT_EXCEPTION_HANDLER)
                        .handle(body.getError());

                setResponseStatusAndHeaders(response, errorResponse);
                String errorResponseBody = getResponseBody(errorResponse).toBlocking().single().getContent();
                return response.writeString(Observable.just(errorResponseBody));
            } else {
                setResponseStatusAndHeaders(response, handlingResult);
                return response.writeString(Observable.just(body.getContent()));
            }
        });
    }

    private Response handleRequest(HttpServerRequest<ByteBuf> httpServerRequest,
                                   HttpServerResponse<ByteBuf> httpServerResponse) {

        try {

            Optional<HttpHandler> httpHandler = resolveHandler(httpServerRequest.getDecodedPath(),
                    httpServerRequest.getHttpMethod());

            return httpHandler
                    .map(handler -> handler.getHttpRouteHandler().handle(
                            new RequestContext(httpServerRequest,
                                    httpServerResponse,
                                    handler.getHttpMapping().resolvePathParams(httpServerRequest.getDecodedPath())
                            )
                    ))
                    .orElse(NOT_FOUND_MAPPING_HANDLER.handle(
                            new RequestContext(httpServerRequest, httpServerResponse, Collections.emptyMap())
                    ));
        } catch (Throwable throwable) {
            log.error("Error occurred!", throwable);

            return errorResponses
                    .getOrDefault(throwable.getClass(), DEFAULT_EXCEPTION_HANDLER)
                    .handle(throwable);
        }
    }

    private void setResponseStatusAndHeaders(HttpServerResponse<ByteBuf> httpServerResponse, Response response) {
        httpServerResponse.setStatus(response.getStatus());
        response.getHeaders().forEach(httpServerResponse::addHeader);
        httpServerResponse.addHeader("Content-Type", response.getContentType().getContentTypeHeader());
    }

    @SuppressWarnings("unchecked")
    private Observable<ResponseBody> getResponseBody(Response handlingResult) {
        Object resultBody = handlingResult.getBody();
        Observable body = resultBody instanceof Observable ? (Observable) resultBody : Observable.just(resultBody);

        Serializer serializer = handlingResult.getContentType().getSerializer();
        return serializer.serialize(body)
                .single()
                .map(content -> ResponseBody.builder()
                        .content((String) content)
                        .build())
                .onErrorReturn(error -> ResponseBody.builder()
                        .error((Throwable) error)
                        .build());
    }

    public HttpRouter addRoute(String route, HttpMethod httpMethod, HttpRouteHandler handler) {

        httpHandlers.add(new HttpHandler(
                new HttpMapping(route, httpMethod), handler)
        );

        return this;
    }

    public HttpRouter addExceptionHandler(Class<? extends Throwable> throwable, ExceptionHandler exceptionHandler) {

        errorResponses.put(throwable, exceptionHandler);
        return this;
    }

    private Optional<HttpHandler> resolveHandler(String url, HttpMethod httpMethod) {

        for (HttpHandler httpHandler : httpHandlers) {
            HttpMapping httpMapping = httpHandler.getHttpMapping();
            if (httpMapping.matches(url, httpMethod)) {
                return Optional.of(httpHandler);
            }
        }

        return Optional.empty();
    }

}
