package core.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static java.util.Objects.isNull;

public class ObjectMapperProvider {

    private static ObjectMapper objectMapper;

    private ObjectMapperProvider() {
    }

    public static synchronized ObjectMapper getObjectMapper() {
        if (isNull(objectMapper)) {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        return objectMapper;
    }
}
