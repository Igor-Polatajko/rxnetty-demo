package core.router.handler;

import core.router.request.RequestContext;
import core.router.response.Response;

@FunctionalInterface
public interface HttpRouteHandler {

    Response handle(RequestContext requestContext);

}
