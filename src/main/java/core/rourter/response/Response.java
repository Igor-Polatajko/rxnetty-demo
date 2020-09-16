package core.rourter.response;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Builder;
import lombok.Data;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Response {

    @Builder.Default
    private Object body = Observable.empty();

    @Builder.Default
    private Map<String, String> headers = new HashMap<>();

    @Builder.Default
    private HttpResponseStatus status = HttpResponseStatus.OK;

    @Builder.Default
    private ContentType contentType = ContentType.JSON;

}
