package core.rourter.request;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import rx.Observable;

import java.util.Optional;

public interface RequestContext {

    Observable deserializeBody(Class clazz);

    HttpServerRequest<ByteBuf> getHttpServerRequest();

    HttpServerResponse<ByteBuf> getHttpServerResponse();

    Object getPathParam(String name, Class<?> clazz);

    Optional<Object> getQueryParam(String name, Class<?> clazz);

}
