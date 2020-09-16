package core.rourter;

import core.rourter.handler.ExceptionHandler;
import core.rourter.handler.HttpRouteHandler;
import core.rourter.response.Response;
import core.rourter.response.ResponseBody;
import core.rourter.response.ResponseType;
import core.serde.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

// ToDo add support for path variables
public class HttpRouter implements RequestHandler<ByteBuf, ByteBuf> {

    private static final HttpRouteHandler NOT_FOUND_MAPPING_HANDLER =
            request -> Response.builder()
                    .body(Observable.just("Route not found"))
                    .responseType(ResponseType.JSON)
                    .status(HttpResponseStatus.NOT_FOUND)
                    .build();

    private static final ExceptionHandler DEFAULT_EXCEPTION_HANDLER =
            throwable -> Response.builder()
                    .body(Observable.just("Internal server error!"))
                    .responseType(ResponseType.JSON)
                    .status(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                    .build();

    private Map<HttpMapping, HttpRouteHandler> routesMap = new HashMap<>();

    private Map<Class<? extends Throwable>, ExceptionHandler> errorResponses = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        HttpMapping httpMapping = new HttpMapping(request.getDecodedPath(), request.getHttpMethod());

        Response handlingResult = handleRequest(request, httpMapping);

        Observable<ResponseBody> responseBody = getResponseBody(handlingResult);

        return responseBody.flatMap(body -> {
            if (body.isError()) {

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

    private Response handleRequest(HttpServerRequest<ByteBuf> request, HttpMapping httpMapping) {
        try {
            return routesMap
                    .getOrDefault(httpMapping, NOT_FOUND_MAPPING_HANDLER)
                    .handle(request); // ToDo request have to be deserialized here as well
        } catch (Throwable throwable) {
            return errorResponses
                    .getOrDefault(throwable.getClass(), DEFAULT_EXCEPTION_HANDLER)
                    .handle(throwable);
        }
    }

    private void setResponseStatusAndHeaders(HttpServerResponse<ByteBuf> httpServerResponse, Response response) {
        httpServerResponse.setStatus(response.getStatus());
        response.getHeaders().forEach(httpServerResponse::addHeader);
        httpServerResponse.addHeader("Content-Type", response.getResponseType().getContentTypeHeader());
    }

    @SuppressWarnings("unchecked")
    private Observable<ResponseBody> getResponseBody(Response handlingResult) {
        Object resultBody = handlingResult.getBody();
        Observable body = resultBody instanceof Observable ? (Observable) resultBody : Observable.just(resultBody);

        Serializer serializer = handlingResult.getResponseType().getSerializer();
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
