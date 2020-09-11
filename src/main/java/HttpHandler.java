import service.ItemService;
import core.rourter.HttpRouter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

// ToDo use Guice for DI
public class HttpHandler {

    private ItemService itemService = new ItemService();

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
