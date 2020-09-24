package handler;

import com.google.inject.Inject;
import core.router.HttpRouter;
import core.router.response.Response;
import exception.NotFoundException;
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
                .addRoute("/items/{id}", HttpMethod.GET, itemResource::findById)
                .addRoute("/items", HttpMethod.POST, itemResource::create)
                .addRoute("/items/{id}", HttpMethod.PUT, itemResource::update)
                .addRoute("/items/{id}", HttpMethod.DELETE, itemResource::delete)
                .addRoute("/upper", HttpMethod.GET, simpleTextResource::toUpperCase)
                .addExceptionHandler(RuntimeException.class, ex -> Response.builder()
                        .status(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                        .body("Runtime exception occurred!")
                        .build())
                .addExceptionHandler(NotFoundException.class, ex -> Response.builder()
                        .status(HttpResponseStatus.BAD_REQUEST)
                        .body(ex.getMessage())
                        .build());

        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8080)
                .start(router);

        server.awaitShutdown();
    }

}
