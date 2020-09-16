package core.rourter.response;

import lombok.Builder;
import lombok.Data;

import static java.util.Objects.nonNull;

@Data
@Builder
public class ResponseBody {

    private String content;

    private Throwable error;

    public boolean isError() {
        return nonNull(error);
    }

}
