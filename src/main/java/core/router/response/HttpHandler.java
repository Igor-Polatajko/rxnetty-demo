package core.router.response;

import core.router.HttpMapping;
import core.router.handler.HttpRouteHandler;
import lombok.Data;

@Data
public class HttpHandler {

    private final HttpMapping httpMapping;

    private final HttpRouteHandler httpRouteHandler;

}
