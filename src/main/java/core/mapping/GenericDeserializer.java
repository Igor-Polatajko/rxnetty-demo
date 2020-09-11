package core.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import rx.Observable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GenericDeserializer {

    private ObjectMapper objectMapper = new ObjectMapper();

    public <T> Observable<T> deserialize(Observable<ByteBuf> body, Class<T> clazz) {
        return body
                .flatMap(content -> {
                    try {
                        String stringContent = content.toString(StandardCharsets.UTF_8);
                        T mappedObject = objectMapper.reader().readValue(stringContent, clazz);
                        return Observable.just(mappedObject);
                    } catch (IOException e) {
                        return Observable.error(new IllegalArgumentException("Cannot deserialize value"));
                    }
                });
    }

}