package com.davidgjm.oss.artifactmanagement.services.maven;

import com.davidgjm.oss.artifactmanagement.ArtifactEntity;
import com.davidgjm.oss.artifactmanagement.ArtifactNotFoundException;

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
    Path find(ArtifactEntity artifact) throws ArtifactNotFoundException;

    /**
     * Check if the specified artifact exists in the local cache.
     * Note that the version number is required for this service.
     * @param artifact The artifact to be searched in the cache
     * @return <b>True</b> if it's available in the local cache. <b>False</b> will always be returned if the version information is missing
     * from the specified artifact.
     */
    boolean isCached(ArtifactEntity artifact);
}
