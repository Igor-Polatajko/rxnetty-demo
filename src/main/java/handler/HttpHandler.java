package handler;

import com.google.inject.Inject;
import core.rourter.HttpRouter;
import core.rourter.response.Response;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import resource.ItemResource;

public class HttpHandler {

    private ItemResource itemResource;

    @Inject
    public HttpHandler(ItemResource itemResource) {
        this.itemResource = itemResource;
    }

    public void run() {

        HttpRouter router = new HttpRouter()
                .addRoute("/items", HttpMethod.GET, itemResource::findAll)
                .addRoute("/item", HttpMethod.GET, itemResource::findById)
                .addRoute("/item", HttpMethod.POST, itemResource::create)
                .addRoute("/item", HttpMethod.PUT, itemResource::update)
                .addRoute("/item", HttpMethod.DELETE, itemResource::delete)
                .addExceptionHandler(RuntimeException.class, ex -> Response.builder()
                        .status(HttpResponseStatus.SERVICE_UNAVAILABLE)
                        .body("Runtime exception occurred!")
                        .build());

        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8080)
                .start(router);

        server.awaitShutdown();
    }

}
