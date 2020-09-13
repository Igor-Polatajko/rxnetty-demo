package handler;

import com.google.inject.Inject;
import core.rourter.HttpRouter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;
import service.ItemService;

public class HttpHandler {

    private ItemService itemService;

    @Inject
    public HttpHandler(ItemService itemService) {
        this.itemService = itemService;
    }

    public void run() {

        HttpRouter router = new HttpRouter()
                .addRoute("/hello",
                        HttpMethod.GET,
                        (request, response) -> response.writeString(Observable.just("Hello from registered route!"))
                )
                .addRoute("/items", HttpMethod.GET, itemService::findAll)
                .addRoute("/item", HttpMethod.GET, itemService::findById)
                .addRoute("/item", HttpMethod.POST, itemService::create)
                .addRoute("/item", HttpMethod.PUT, itemService::update)
                .addRoute("/item", HttpMethod.DELETE, itemService::delete);

        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8080)
                .start(router);

        server.awaitShutdown();
    }

}
