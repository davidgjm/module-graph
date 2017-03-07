package com.davidgjm.oss.maven.providers;

import com.davidgjm.oss.maven.domain.Module;
import com.davidgjm.oss.maven.support.ArtifactSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by david on 2017/3/4.
 */
public abstract class BaseRemoteRepositoryProvider implements RemoteRepositoryProvider{
    protected final Logger logger= LoggerFactory.getLogger(getClass());
    protected URL baseUrl;
    private final DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();


    @PostConstruct
    private void init() {
        try {
            initBaseUrl();
        } catch (MalformedURLException e) {
            logger.error("Failed to initialize base URL.", e);
            throw new RuntimeException(e);
        }

        logger.debug("{} - Base URL: {}",getClass().getName(), baseUrl);
        if (baseUrl==null) {
            throw new IllegalStateException("Base URL not defined!");
        }
    }

    /**
     * Initializes base URL in this method.
     */
    protected abstract void initBaseUrl() throws MalformedURLException;

    @Override
    public URL get() {
        return baseUrl;
    }

    /**
     * Attempts to get the latest release from the file <i>maven-metadata.xml</i>.
     * @param artifact The artifact to be parsed
     * @return The version number for the artifact.
     */
    protected String parseVersionFromMetadata(Module artifact){
        String relativePathToArtifact = ArtifactSupport.getRelativeUrlFragment(artifact.getGroupId(), artifact.getArtifactId());
        logger.debug("{} - Relative artifact: {}",getClass().getName(), relativePathToArtifact);

        //parses the maven metadata remotely.
        Document metadataDocument = parseMavenMetadata(artifact);
        String version = metadataDocument.getElementsByTagName("release").item(0).getTextContent().trim();
        logger.info("Parsed version for artifact [{}:{}]:  {}",artifact.getGroupId(), artifact.getArtifactId(), version);
        return version;
    }

    private Document parseMavenMetadata(Module artifact) {
        String relativePathToArtifact = ArtifactSupport.getRelativeUrlFragment(artifact.getGroupId(), artifact.getArtifactId());

        String completeUrl = String.format("%s/%s/maven-metadata.xml", baseUrl.toString(), relativePathToArtifact.replace("//","/"))
                ;
        logger.debug("{} - URL to artifact: [{}]", getClass().getName(), completeUrl);
        Document document;
        try {
            URL url = new URL(completeUrl);
            InputStream is = url.openStream();
            document = factory.newDocumentBuilder().parse(is);
        } catch (IOException | SAXException| ParserConfigurationException e) {
            logger.error("Failed to parse remote maven metadata", e);
            throw new RuntimeException(e);
        }
        return document;
    }
}
