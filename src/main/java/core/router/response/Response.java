package core.router.response;

import core.router.ContentType;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Builder;
import lombok.Data;
import rx.Completable;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Response {

    @Builder.Default
    private Object body = Completable.complete();

    @Builder.Default
    private Map<String, String> headers = new HashMap<>();

    @Builder.Default
    private HttpResponseStatus status = HttpResponseStatus.OK;

    @Builder.Default
    private ContentType contentType = ContentType.JSON;

}
