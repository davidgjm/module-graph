package com.davidgjm.oss.artifactmanagement.services.maven;

import com.davidgjm.oss.artifactmanagement.ArtifactNotFoundException;
import com.davidgjm.oss.artifactmanagement.configuration.ArtifactRepositoryProperties;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by david on 2017/4/8.
 */
@Service
public class PomLocatingServiceImpl implements PomLocatingService{
    private final Logger logger= LoggerFactory.getLogger(getClass());
    private final ArtifactRepositoryProperties repositoryProperties;

    public PomLocatingServiceImpl(ArtifactRepositoryProperties repositoryProperties) {
        this.repositoryProperties = repositoryProperties;
    }

    @Override
    public Path locate(@NotNull @NotBlank String groupId, @NotNull @NotBlank String artifactId, Optional<String> versionOptional) throws ArtifactNotFoundException {
        return null;
    }
}
