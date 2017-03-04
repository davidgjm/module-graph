package com.davidgjm.oss.maven.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Created by david on 2017/3/4.
 */
public class RemotePomFile {
    private URL baseUrl;
    private String pomPath;

    public RemotePomFile(URL baseUrl, String pomPath) {
        this.baseUrl = baseUrl;
        this.pomPath = pomPath;
    }

    public RemotePomFile() {
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPomPath() {
        return pomPath;
    }

    public void setPomPath(String pomPath) {
        this.pomPath = pomPath;
    }

    public URL toAbsoluteUrl() throws MalformedURLException {
        String base = baseUrl.toString();
        StringBuilder path = new StringBuilder(base);
        if (!base.endsWith("/")) {
            path.append("/");
        }
        path.append(pomPath);
        return new URL(path.toString());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemotePomFile that = (RemotePomFile) o;
        return Objects.equals(getBaseUrl(), that.getBaseUrl()) &&
                Objects.equals(getPomPath(), that.getPomPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBaseUrl(), getPomPath());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RemotePomFile{");
        sb.append("baseUrl=").append(baseUrl);
        sb.append(", pomPath='").append(pomPath).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
