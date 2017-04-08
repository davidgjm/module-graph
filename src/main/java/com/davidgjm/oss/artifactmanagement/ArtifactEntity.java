package com.davidgjm.oss.artifactmanagement;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by david on 2017/3/7.
 */
public interface ArtifactEntity {
    void setGroupId(String groupId);

    @NotNull
    @NotBlank
    String getGroupId();

    void setArtifactId(String artifactId);

    @NotNull
    @NotBlank
    String getArtifactId();

    void setVersion(String version);

    @NotBlank
    String getVersion();

    void setName(String name);

    String getName();

    default boolean hasGroupId(){
        return getGroupId()!=null && !getGroupId().isEmpty();
    }
}
