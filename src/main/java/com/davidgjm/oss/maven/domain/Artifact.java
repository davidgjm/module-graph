package com.davidgjm.oss.maven.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * <div>
 * Created with IntelliJ IDEA.
 * User: Jian-Min Gao <br>
 * Date: 2017/3/2 <br>
 * Time: 18:26 <br>
 * </div>
 */

public class Artifact implements Serializable{
    private String groupId;
    private String artifactId;
    private String version;

    public Artifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Artifact(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public Artifact() {
    }

    /**
     * Getter for property 'groupId'.
     *
     * @return Value for property 'groupId'.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Setter for property 'groupId'.
     *
     * @param groupId Value to set for property 'groupId'.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Module toModule() {
        return new Module(groupId, artifactId, version);
    }

    /**
     * Getter for property 'artifactId'.
     *
     * @return Value for property 'artifactId'.
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Setter for property 'artifactId'.
     *
     * @param artifactId Value to set for property 'artifactId'.
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Getter for property 'version'.
     *
     * @return Value for property 'version'.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Setter for property 'version'.
     *
     * @param version Value to set for property 'version'.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artifact that = (Artifact) o;

        return Objects.equals(this.artifactId, that.artifactId) &&
                Objects.equals(this.groupId, that.groupId) &&
                Objects.equals(this.version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, groupId, version);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("artifactId = " + artifactId)
                .add("groupId = " + groupId)
                .add("version = " + version)
                .toString();
    }
}