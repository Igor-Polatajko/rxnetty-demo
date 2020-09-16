package handler;

import com.google.inject.Inject;
import core.rourter.HttpRouter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
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
                .addRoute("/item", HttpMethod.DELETE, itemResource::delete);

        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8080)
                .start(router);

        server.awaitShutdown();
    }

}
