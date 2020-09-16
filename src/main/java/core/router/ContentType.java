package core.router;

import core.serde.Deserializer;
import core.serde.JsonDeserializer;
import core.serde.JsonSerializer;
import core.serde.PlainTextDeserializer;
import core.serde.Serializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


@Getter
@AllArgsConstructor
public enum ContentType {

    JSON("application/json", new JsonSerializer(), new JsonDeserializer()),
    PLAIN_TEXT("text/plain", new JsonSerializer(), new PlainTextDeserializer());

    private String contentTypeHeader;

    private Serializer serializer;

    private Deserializer deserializer;

    public static ContentType resolveByHeaderValue(String value) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.getContentTypeHeader().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported data type"));
    }

}
