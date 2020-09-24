package core.router.request;

import core.router.ContentType;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import rx.Observable;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestContext {

    private HttpServerRequest<ByteBuf> httpServerRequest;

    private HttpServerResponse<ByteBuf> httpServerResponse;

    public Observable deserializeBody(Class clazz) {
        return ContentType.resolveByHeaderValue(httpServerRequest.getHeader("Content-Type"))
                .getDeserializer()
                .deserialize(httpServerRequest.getContent(), clazz);
    }

    public HttpServerRequest<ByteBuf> getHttpServerRequest() {
        return httpServerRequest;
    }

    public HttpServerResponse<ByteBuf> getHttpServerResponse() {
        return httpServerResponse;
    }

    public Object getPathParam(String name) {
        return null;
    }

    public String getQueryParam(String name) {
        return getQueryParamAsList(name).get(0);
    }

    public List<String> getQueryParamAsList(String name) {
        return httpServerRequest.getQueryParameters().get(name);
    }

}
