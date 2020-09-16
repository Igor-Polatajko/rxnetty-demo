package core.serde;

import io.netty.buffer.ByteBuf;
import rx.Observable;

public interface Deserializer {

   Observable deserialize(Observable<ByteBuf> body, Class<Object> clazz);

}
