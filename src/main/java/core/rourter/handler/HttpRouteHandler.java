package core.rourter.handler;

import core.rourter.request.RequestContext;
import core.rourter.response.Response;

@FunctionalInterface
public interface HttpRouteHandler {

    Response handle(RequestContext requestContext);

}
