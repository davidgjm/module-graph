package com.davidgjm.oss.maven.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by david on 2017/3/4.
 */
public class CacheSettings implements Serializable{
    private String filename;
    private String directory;

    public CacheSettings(String directory, String filename) {
        this.filename = filename;
        this.directory = directory;
    }

    public CacheSettings() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheSettings that = (CacheSettings) o;
        return Objects.equals(getFilename(), that.getFilename()) &&
                Objects.equals(getDirectory(), that.getDirectory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilename(), getDirectory());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CacheSettings{");
        sb.append("filename='").append(filename).append('\'');
        sb.append(", directory='").append(directory).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
