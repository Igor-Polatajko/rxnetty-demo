package core.rourter;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;

import java.util.Objects;

@Data
public final class HttpMapping {

    private final String httpUrl;

    private final HttpMethod httpMethod;

}
