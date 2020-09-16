package core.rourter;

import core.rourter.response.Response;
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

    private static final HttpRouteHandler DEFAULT_HANDLER =
            request -> Response.builder()
                    .body(Observable.just("Route not found"))
                    .responseType(ResponseType.JSON)
                    .status(HttpResponseStatus.NOT_FOUND)
                    .build();

    private Map<HttpMapping, HttpRouteHandler> routesMap = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        HttpMapping httpMapping = new HttpMapping(request.getDecodedPath(), request.getHttpMethod());

        Response resultResponse = routesMap
                .getOrDefault(httpMapping, DEFAULT_HANDLER)
                .handle(request);

        response.setStatus(resultResponse.getStatus());
        resultResponse.getHeaders().forEach(response::addHeader);
        response.addHeader("Content-Type", resultResponse.getResponseType().getContentTypeHeader());

        Object resultBody = resultResponse.getBody();
        Observable body = resultBody instanceof Observable ? (Observable) resultBody : Observable.just(resultBody);

        Serializer serializer = resultResponse.getResponseType().getSerializer();
        return response.writeString(serializer.serialize(body));
    }

    public HttpRouter addRoute(String route, HttpMethod httpMethod, HttpRouteHandler handler) {

        routesMap.put(new HttpMapping(route, httpMethod), handler);
        return this;
    }
}
