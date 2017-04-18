package com.davidgjm.oss.artifactmanagement.services.maven;

import com.davidgjm.oss.artifactmanagement.ArtifactEntity;
import com.davidgjm.oss.artifactmanagement.ArtifactNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;

/**
 * Created by david on 2017/4/8.
 */
public interface PomCacheService {

    /**
     * Finds the specified artifact in the local cache.
     * The artifact pom file will be search in the local cache.
     * @param artifact The artifact to be searched.
     * @return The path to the cache pom file.
     * @throws ArtifactNotFoundException If the artifact is not cache. In other words, the pom file does not exist locally for
     * the artifact.
     */
    Path find(@NotNull @Valid ArtifactEntity artifact) throws ArtifactNotFoundException;

    /**
     * Caches the specified artifact into local cache store.
     * Nothing should happen if the corresponding artifact is already cached. The latest release version should be
     * cached if version information is missing.
     * <p>
     *     <b>Note</b>: The matadata.xml file should also be downloaded and updated accordingly every time an artifact is being cached.
     *     This file will also be used for deciding the latest version of the subject artifact
     * </p>
     * @param artifact The artifact should be cached.
     * @return The local path to the cached artifact.
     */
    Path put(@NotNull @Valid ArtifactEntity artifact);

    /**
     * Check if the specified artifact exists in the local cache.
     * Note that the version number is required for this service.
     * @param artifact The artifact to be searched in the cache
     * @return <b>True</b> if it's available in the local cache. <b>False</b> will always be returned if the version information is missing
     * from the specified artifact.
     */
    boolean isCached(@NotNull @Valid ArtifactEntity artifact);
}
