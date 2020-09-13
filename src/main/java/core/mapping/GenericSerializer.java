package core.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import rx.Observable;

public class GenericSerializer {

    private ObjectMapper objectMapper;

    @Inject
    public GenericSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> Observable<String> serialize(Observable<T> object) {
        return object
                .flatMap(obj -> {
                    try {
                        return Observable.just(objectMapper.writer().writeValueAsString(obj));
                    } catch (JsonProcessingException e) {
                        return Observable.error(new IllegalArgumentException("Cannot serialize value"));
                    }
                });
    }

}
