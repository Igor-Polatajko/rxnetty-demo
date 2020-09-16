package core.rourter.handler;

import core.rourter.response.Response;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;

@FunctionalInterface
public interface HttpRouteHandler {

    Response handle(HttpServerRequest<ByteBuf> request);

}
