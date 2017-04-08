package com.davidgjm.oss.artifactmanagement.services.maven;

import com.davidgjm.oss.artifactmanagement.ArtifactEntity;
import com.davidgjm.oss.artifactmanagement.ArtifactNotFoundException;
import com.davidgjm.oss.artifactmanagement.support.ArtifactSupport;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by david on 2017/4/8.
 */
public interface PomLocatingService {
    /**
     * Locates the artifact specified via group id, artifact id and version in the local storage.
     * If the artifact does not exist locally, this service will try to fetch online from repositories specified
     * in the configuration file. If this still fails, exception will be thrown.
     * @param groupId Required. The groupId for the artifact.
     * @param artifactId Required. The artifact id.
     * @param versionOptional The optional version number. If the version number is not available, the latest release version should be checked and returned.
     * @return The local path to the specified artifact. Exception will be thrown if the artifact cannot be found in all means.
     * @throws ArtifactNotFoundException if the artifact is not found.
     */
    Path locate(@NotNull @NotBlank String groupId, @NotNull @NotBlank String artifactId, Optional<String> versionOptional) throws ArtifactNotFoundException;

    default Path locate(ArtifactEntity artifact) {
        Objects.requireNonNull(artifact, "The artifact to be located cannot be null");
        ArtifactSupport.validate(artifact);
        return locate(artifact.getGroupId(), artifact.getArtifactId(), Optional.ofNullable(artifact.getVersion()));
    }
}
