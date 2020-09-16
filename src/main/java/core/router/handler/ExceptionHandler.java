package core.router.handler;

import core.router.response.Response;

@FunctionalInterface
public interface ExceptionHandler {

    Response handle(Throwable throwable);

}
