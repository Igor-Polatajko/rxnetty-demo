package core.router;

import lombok.Data;

@Data
public class PathParamDescriptor {

    private final int position;

    private final String name;

}
