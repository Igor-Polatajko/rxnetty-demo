package core.router;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class HttpMapping {

    private final String urlRegExp;

    private final HttpMethod httpMethod;

    private final List<PathParamDescriptor> paramDescriptors;

    public HttpMapping(String url, HttpMethod httpMethod) {
        this.urlRegExp = computeRegExp(url);
        this.paramDescriptors = computePathParamDescriptors(url);
        this.httpMethod = httpMethod;
    }

    private String computeRegExp(String url) {
        return url.replaceAll("\\{.*?}", ".*?");
    }

    private List<PathParamDescriptor> computePathParamDescriptors(String url) {
        String[] urlSections = url.split("/");

        List<PathParamDescriptor> pathParamDescriptors = new ArrayList<>();

        for (int i = 0; i < urlSections.length; i++) {
            String urlSection = urlSections[i];
            if (urlSection.startsWith("{") && urlSection.endsWith("}"))
                pathParamDescriptors.add(
                        new PathParamDescriptor(i, urlSection.substring(1, urlSection.length() - 1))
                );
        }

        return pathParamDescriptors;
    }

    public boolean matches(String requestUrl, HttpMethod httpMethod) {
        return this.httpMethod.equals(httpMethod) && requestUrl.matches(this.urlRegExp);
    }

    public Map<String, String> resolvePathParams(String requestUrl) {

        String[] urlSections = requestUrl.split("/");

        Map<String, String> pathParams = new HashMap<>();
        for (PathParamDescriptor paramDescriptor : paramDescriptors) {
            pathParams.put(paramDescriptor.getName(), urlSections[paramDescriptor.getPosition()]);
        }

        return pathParams;
    }

}
