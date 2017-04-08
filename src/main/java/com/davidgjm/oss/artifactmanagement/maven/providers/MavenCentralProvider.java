package com.davidgjm.oss.artifactmanagement.maven.providers;

import com.davidgjm.oss.artifactmanagement.ArtifactEntity;
import com.davidgjm.oss.artifactmanagement.domain.RemotePomFile;
import com.davidgjm.oss.artifactmanagement.support.ArtifactSupport;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by david on 2017/3/4.
 */
@Service
@Primary
public class MavenCentralProvider extends BaseRemoteRepositoryProvider{
    private static final String BASE_URL = "http://repo1.maven.org/maven2";
    @Override
    protected void initBaseUrl() throws MalformedURLException {
        baseUrl = new URL(BASE_URL);
    }

    @Override
    public RemotePomFile getRemoteArtifactPom(ArtifactEntity artifact) {
        ArtifactSupport.validate(artifact);
        String version = artifact.getVersion();
        if (!StringUtils.hasText(version)) {
            version = parseVersionFromMetadata(artifact);
        }
        String pomUrl = ArtifactSupport.getRelativeUrlFragment(artifact.getGroupId(), artifact.getArtifactId(), version);
        logger.debug("{} - Pom URL: {}",getClass().getName(), pomUrl);
        RemotePomFile remotePomFile = new RemotePomFile(baseUrl, pomUrl);
        logger.debug("{} - Remote pom file: [{}]",getClass().getName(), remotePomFile);
        return remotePomFile;
    }

}
