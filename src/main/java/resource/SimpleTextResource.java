package resource;

import core.router.request.RequestContext;
import core.router.response.Response;
import rx.Completable;
import rx.Observable;

public class SimpleTextResource {

    public Response toUpperCase(RequestContext requestContext) {

        Observable<String> upperCaseString = requestContext.deserializeBody(String.class)
                .map(inputString -> ((String) inputString).toUpperCase());

        return Response.builder()
                .body(upperCaseString)
                .build();
    }

    public Response testCompletableError(RequestContext requestContext) {

        return Response.builder()
                .body(Completable.error(new RuntimeException("Works fine!")))
                .build();
    }

}
