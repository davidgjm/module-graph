package com.davidgjm.oss.maven;

/**
 * Created by david on 2017/3/7.
 */
public interface ArtifactEntity {
    void setGroupId(String groupId);

    String getGroupId();

    void setArtifactId(String artifactId);

    String getArtifactId();

    void setVersion(String version);

    String getVersion();

    void setName(String name);

    String getName();
}
