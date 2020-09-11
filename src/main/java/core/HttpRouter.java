package core;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

public class HttpRouter implements RequestHandler<ByteBuf, ByteBuf> {

    private static final HttpRouteHandler DEFAULT_HANDLER =
            (req, resp) -> resp.writeString(Observable.just("Route not found"));

    private Map<HttpMapping, HttpRouteHandler> routesMap = new HashMap<>();

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

        HttpMapping httpMapping = new HttpMapping(request.getDecodedPath(), HttpMethod.GET);
        return routesMap
                .getOrDefault(httpMapping, DEFAULT_HANDLER)
                .handle(request, response);
    }

    public HttpRouter addRoute(String route, HttpMethod httpMethod, HttpRouteHandler handler) {

        routesMap.put(new HttpMapping(route, httpMethod), handler);
        return this;
    }
}
