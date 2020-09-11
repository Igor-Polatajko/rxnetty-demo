import core.HttpRouter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

public class Application {

    public static void main(String[] args) {

        HttpRouter router = new HttpRouter()
                .addRoute("/hello",
                        HttpMethod.GET,
                        (req, resp) -> resp.writeString(Observable.just("Hello from registered route!"))
                );

        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8080)
                .start(router);

        server.awaitShutdown();
    }

}
