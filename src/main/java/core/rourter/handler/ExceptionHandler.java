package core.rourter.handler;

import core.rourter.response.Response;

@FunctionalInterface
public interface ExceptionHandler {

    Response handle(Throwable throwable);

}
