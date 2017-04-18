package com.davidgjm.oss.artifactmanagement.services.maven;

import com.davidgjm.oss.artifactmanagement.configuration.ArtifactRepositoryProperties;
import com.davidgjm.oss.artifactmanagement.domain.Artifact;
import com.davidgjm.oss.artifactmanagement.domain.ArtifactRepository;
import com.davidgjm.oss.artifactmanagement.services.maven.parsers.MetadataParser;
import com.davidgjm.oss.artifactmanagement.support.ArtifactSupport;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by david on 2017/4/18.
 */
@Component
public class MetaFileDownloader {
    private final Logger logger= LoggerFactory.getLogger(getClass());
    @Value("${application.configuration.artifact.cache-location}")
    private String cacheLocation;

    private final ArtifactRepositoryProperties repositoryProperties;
    private final CloseableHttpClient httpClient;
    private MetadataParser metadataParser;

    public MetaFileDownloader(ArtifactRepositoryProperties repositoryProperties, CloseableHttpClient httpClient) {
        this.repositoryProperties = repositoryProperties;
        this.httpClient = httpClient;
    }

    @Autowired
    public void setMetadataParser(MetadataParser metadataParser) {
        this.metadataParser = metadataParser;
    }

    public Path downloadGroupMetadata(@NotNull @Valid Artifact artifact) {
        ArtifactSupport.validate(artifact);
        String filename = "maven-metadata.xml";
        String groupUrl = ArtifactSupport.getRelativeUrlFragment(artifact.getGroupId(), artifact.getArtifactId());

        //Create local directory structure first
        Path groupIdFolder = Paths.get(cacheLocation, groupUrl);
        Path dataFile = Paths.get(cacheLocation, groupUrl, filename);

        if (Files.exists(dataFile)) {
            logger.info("maven-metadata file already exists. {}",dataFile);
        }

        try {
            Files.createDirectories(groupIdFolder);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to create group id folder structure. ",e);
        }

        List<ArtifactRepository> repositories=repositoryProperties.getRepositories().stream().filter(r -> !r.isLocal())
                .collect(Collectors.toList());

        boolean isDownloaded=false;
        for (ArtifactRepository repository : repositories) {
            String relativeUrl = groupUrl + "/" + filename;
            String baseUrl = repository.getPath().replaceAll("/$", "");
            String url=String.format ("%s/%s", baseUrl, relativeUrl);
            logger.debug("Maven-metadata.xml url: {}", url);

            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(request);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() != 200) {//try next repository
                    logger.error("Server response: {}", statusLine);
                    continue;
                }

                HttpEntity entity = response.getEntity();
                FileOutputStream fos = new FileOutputStream(dataFile.toFile());
                entity.writeTo(fos);
                fos.close();
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Problem downloading maven-metadata.xml", e);
                continue;
            }

            isDownloaded = true;
            logger.debug("Maven metadata file downloaded from repository: {}", repository);
            break;
        }

        if (!isDownloaded) {
            String msg = String.format("Failed to download group metadata file for %s", artifact);
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        return dataFile;
    }

}
