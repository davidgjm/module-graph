package com.davidgjm.oss.artifactmanagement.domain;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * <div>
 * Created with IntelliJ IDEA.
 * User: Jian-Min Gao <br>
 * Date: 2017/4/6 <br>
 * Time: 9:39 <br>
 * </div>
 */

public class ArtifactRepository {
    private boolean isLocal;
    private String path;

    public ArtifactRepository(String path, boolean isLocal) {
        this.isLocal = isLocal;
        this.path = path;
    }

    public ArtifactRepository(String path) {
        this.path = path;
    }

    public ArtifactRepository() {
    }

    /**
     * Getter for property 'local'.
     *
     * @return Value for property 'local'.
     */
    public boolean isLocal() {
        return isLocal;
    }

    /**
     * Setter for property 'local'.
     *
     * @param local Value to set for property 'local'.
     */
    public void setLocal(boolean local) {
        isLocal = local;
    }

    /**
     * Getter for property 'path'.
     *
     * @return Value for property 'path'.
     */
    public String getPath() {
        return path;
    }

    /**
     * Setter for property 'path'.
     *
     * @param path Value to set for property 'path'.
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtifactRepository that = (ArtifactRepository) o;

        return Objects.equals(this.isLocal, that.isLocal) &&
                Objects.equals(this.path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isLocal, path);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("isLocal = " + isLocal)
                .add("path = " + path)
                .toString();
    }
}
