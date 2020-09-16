package core.rourter.response;

import core.serde.GenericSerializer;
import core.serde.Serializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseType {

    JSON("application/json", new GenericSerializer());

    private String contentTypeHeader;

    private Serializer serializer;

}
