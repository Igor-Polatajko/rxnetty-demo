package core.router.request;

import core.router.ContentType;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.isNull;

@Data
@AllArgsConstructor
public class RequestContextImpl implements RequestContext {

    private static final Map<Class, Function<String, ?>> TYPE_CONVERTER = new HashMap<Class, Function<String, ?>>() {{
        put(Long.class, Long::valueOf);
        put(Integer.class, Integer::valueOf);
        put(Double.class, Double::valueOf);
        put(Boolean.class, Boolean::valueOf);
        put(String.class, String::valueOf);
    }};

    private HttpServerRequest<ByteBuf> httpServerRequest;

    private HttpServerResponse<ByteBuf> httpServerResponse;

    @Override
    public Observable deserializeBody(Class clazz) {
        return ContentType.resolveByHeaderValue(httpServerRequest.getHeader("Content-Type"))
                .getDeserializer()
                .deserialize(httpServerRequest.getContent(), clazz);
    }

    @Override
    public HttpServerRequest<ByteBuf> getHttpServerRequest() {
        return httpServerRequest;
    }

    @Override
    public HttpServerResponse<ByteBuf> getHttpServerResponse() {
        return httpServerResponse;
    }

    @Override
    public Object getPathParam(String name, Class<?> clazz) {
        return null;
    }

    @Override
    public Optional<Object> getQueryParam(String name, Class<?> clazz) {

        List<String> queryParams = httpServerRequest.getQueryParameters().get(name);

        if (isNull(queryParams)) {
            return Optional.empty();
        }

        if (clazz == List.class) {
            return Optional.of(queryParams);
        }

        String queryParam = queryParams.get(0);
        if (TYPE_CONVERTER.containsKey(clazz)) {
            return Optional.of(TYPE_CONVERTER.get(clazz).apply(queryParam));
        }

        return Optional.empty();
    }

}
