package core.router.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import static java.util.Objects.nonNull;

@Data
@Builder(access = AccessLevel.PRIVATE)
public class ResponseBody {

    private final String content;

    private final Throwable error;

    public boolean isError() {
        return nonNull(error);
    }

    public static ResponseBody success(String content) {
        return ResponseBody.builder()
                .content(content)
                .build();
    }

    public static ResponseBody error(Throwable error) {
        return ResponseBody.builder()
                .error(error)
                .build();
    }

}
