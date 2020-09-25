package core.router.request;

import core.router.ContentType;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import lombok.Data;
import rx.Observable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class RequestContext {

    private final HttpServerRequest<ByteBuf> httpServerRequest;

    private final HttpServerResponse<ByteBuf> httpServerResponse;

    private final Map<String, String> pathParams;

    public Observable deserializeBody(Class clazz) {
        return ContentType.resolveByHeaderValue(httpServerRequest.getHeader("Content-Type"))
                .getDeserializer()
                .deserialize(httpServerRequest.getContent(), clazz);
    }

    public String getPathParam(String name) {
        return pathParams.get(name);
    }

    public String getQueryParam(String name) {
        return Optional.ofNullable(getQueryParamAsList(name))
                .map(params -> params.get(0))
                .orElse(null);
    }

    public List<String> getQueryParamAsList(String name) {
        return httpServerRequest.getQueryParameters().get(name);
    }

}
