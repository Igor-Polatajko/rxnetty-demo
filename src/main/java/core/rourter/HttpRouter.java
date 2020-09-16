package core.rourter;

import core.rourter.handler.ExceptionHandler;
import core.rourter.handler.HttpRouteHandler;
import core.rourter.request.RequestContext;
import core.rourter.request.RequestContextImpl;
import core.rourter.response.ContentType;
import core.rourter.response.Response;
import core.rourter.response.ResponseBody;
import core.serde.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

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
                    .body("Internal server error!")
                    .contentType(ContentType.JSON)
                    .status(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                    .build();

    private Map<HttpMapping, HttpRouteHandler> routesMap = new HashMap<>();

    private Map<Class<? extends Throwable>, ExceptionHandler> errorResponses = new HashMap<>();

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        HttpMapping httpMapping = new HttpMapping(request.getDecodedPath(), request.getHttpMethod());

        Response handlingResult = handleRequest(request, response, httpMapping);

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
                                   HttpServerResponse<ByteBuf> httpServerResponse, HttpMapping httpMapping) {

        try {

            RequestContext requestContext = new RequestContextImpl(httpServerRequest, httpServerResponse);

            return routesMap
                    .getOrDefault(httpMapping, NOT_FOUND_MAPPING_HANDLER)
                    .handle(requestContext);
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

        routesMap.put(new HttpMapping(route, httpMethod), handler);
        return this;
    }

    public HttpRouter addExceptionHandler(Class<? extends Throwable> throwable, ExceptionHandler exceptionHandler) {

        errorResponses.put(throwable, exceptionHandler);
        return this;
    }
}
