package core.serde;

import io.netty.buffer.ByteBuf;
import rx.Observable;

import java.nio.charset.StandardCharsets;

public class PlainTextDeserializer implements Deserializer {

    @Override
    public Observable deserialize(Observable<ByteBuf> body, Class<Object> clazz) {
        return body.map(content -> content.toString(StandardCharsets.UTF_8));
    }

}
