package core.router;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;

@Data
public final class HttpMapping {

    private final String httpUrl;

    private final HttpMethod httpMethod;

}
