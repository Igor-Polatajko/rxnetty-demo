package core;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Objects;

public final class HttpMapping {

    private final String httpUrl;

    private final HttpMethod httpMethod;

    public HttpMapping(String httpUrl, HttpMethod httpMethod) {
        this.httpUrl = httpUrl;
        this.httpMethod = httpMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpMapping that = (HttpMapping) o;
        return Objects.equals(httpUrl, that.httpUrl) &&
                Objects.equals(httpMethod, that.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpUrl, httpMethod);
    }
}
