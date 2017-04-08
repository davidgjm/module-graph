package com.davidgjm.oss.artifactmanagement.services.maven;

import com.davidgjm.oss.artifactmanagement.ArtifactEntity;
import com.davidgjm.oss.artifactmanagement.ArtifactNotFoundException;
import com.davidgjm.oss.artifactmanagement.support.ArtifactSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Created by david on 2017/4/8.
 */
@Service
@Primary
public class PomCacheServiceImpl implements PomCacheService {
    private final Logger logger= LoggerFactory.getLogger(getClass());

    @Value("${application.configuration.artifact.cache-location}")
    private String cacheLocation;

    @Override
    public Path find(ArtifactEntity artifact) throws ArtifactNotFoundException {
        validate(artifact);
        if (!isCached(artifact)) {
            logger.warn("Artifact not cached! {}",artifact);
            throw new ArtifactNotFoundException("Artifact not cached. " + artifact);
        }
        return getArtifactPath(artifact);
    }

    @Override
    public boolean isCached(ArtifactEntity artifact) {
        ArtifactSupport.validate(artifact);
        if(!StringUtils.hasText(artifact.getVersion())) return false;
        Path cachedPath = getArtifactPath(artifact);
        logger.debug("{} - Computed cached artifact path: [{}]",getClass().getName(), cachedPath);
        return Files.exists(cachedPath);
    }

    private String getArtifactRelativePath(ArtifactEntity artifact) {
        Objects.requireNonNull(artifact);
        if (!StringUtils.hasText(artifact.getVersion())) {
            logger.error("Missing version information. {}", artifact);
            throw new IllegalStateException("Version information is required!");
        }
        return ArtifactSupport.getRelativeUrlFragment(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }

    private Path getArtifactPath(ArtifactEntity artifact) {
        return Paths.get(cacheLocation, getArtifactRelativePath(artifact));
    }

    private void validate(ArtifactEntity entity) {
        ArtifactSupport.validate(entity);
        if (!StringUtils.hasText(entity.getVersion())) {
            logger.error("Missing version information. {}", entity);
            throw new IllegalStateException("Version number is required for cached artifacts!");
        }
    }
}
