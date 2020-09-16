package resource;

import core.rourter.request.RequestContext;
import core.rourter.response.Response;
import rx.Observable;

public class SimpleTextResource {

    public Response toUpperCase(RequestContext requestContext) {

        Observable<String> upperCaseString = requestContext.deserializeBody(String.class)
                .map(inputString -> ((String) inputString).toUpperCase());

        return Response.builder()
                .body(upperCaseString)
                .build();
    }

}
