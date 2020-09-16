package core.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import rx.Observable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonDeserializer implements Deserializer {

    private ObjectMapper objectMapper = ObjectMapperProvider.getObjectMapper();

    public Observable deserialize(Observable<ByteBuf> body, Class<Object> clazz) {
        return body
                .flatMap(content -> {
                    try {
                        String stringContent = content.toString(StandardCharsets.UTF_8);
                        Object mappedObject = objectMapper.reader().readValue(stringContent, clazz);
                        return Observable.just(mappedObject);
                    } catch (IOException e) {
                        return Observable.error(new IllegalArgumentException("Cannot deserialize value"));
                    }
                });
    }

}