package handler;

import com.google.inject.Inject;
import core.rourter.HttpRouter;
import core.rourter.response.Response;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import resource.ItemResource;
import resource.SimpleTextResource;

public class HttpHandler {

    private ItemResource itemResource;

    private SimpleTextResource simpleTextResource;

    @Inject
    public HttpHandler(ItemResource itemResource, SimpleTextResource simpleTextResource) {
        this.itemResource = itemResource;
        this.simpleTextResource = simpleTextResource;
    }

    public void run() {

        HttpRouter router = new HttpRouter()
                .addRoute("/items", HttpMethod.GET, itemResource::findAll)
                .addRoute("/item", HttpMethod.GET, itemResource::findById)
                .addRoute("/item", HttpMethod.POST, itemResource::create)
                .addRoute("/item", HttpMethod.PUT, itemResource::update)
                .addRoute("/item", HttpMethod.DELETE, itemResource::delete)
                .addRoute("/upper", HttpMethod.GET, simpleTextResource::toUpperCase)
                .addExceptionHandler(RuntimeException.class, ex -> Response.builder()
                        .status(HttpResponseStatus.SERVICE_UNAVAILABLE)
                        .body("Runtime exception occurred!")
                        .build());

        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8080)
                .start(router);

        server.awaitShutdown();
    }

}
