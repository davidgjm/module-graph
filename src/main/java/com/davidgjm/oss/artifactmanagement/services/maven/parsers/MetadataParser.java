package com.davidgjm.oss.artifactmanagement.services.maven.parsers;

import com.davidgjm.oss.artifactmanagement.ArtifactEntity;
import com.davidgjm.oss.artifactmanagement.domain.MavenMetadata;
import com.davidgjm.oss.artifactmanagement.support.ArtifactSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by david on 2017/4/18.
 */
@Component
public class MetadataParser extends BaseXmlParser {
    private final Logger logger= LoggerFactory.getLogger(getClass());
    @Value("${application.configuration.artifact.cache-location}")
    private String cacheLocation;


    public MavenMetadata parse(ArtifactEntity artifact) {
        ArtifactSupport.validate(artifact);
        Path metadataFile = getMetadataFilePath(artifact);
        if (Files.notExists(metadataFile)) {
            throw new RuntimeException(new NoSuchFileException(metadataFile.toString()));
        }

        Document document = parseXml(metadataFile);
        Element root = document.getDocumentElement();
        MavenMetadata metadata = new MavenMetadata();
        metadata.setGroupId(getGroupId(root));
        metadata.setArtifactId(getArtifactId(root));
        metadata.setLastUpdated(getLastUpdated(root));
        try {
            metadata.setLatest(getLatest(root));
            metadata.setRelease(getRelease(root));
        } catch (XPathExpressionException e) {
            logger.error("{}",e);
            throw new RuntimeException("Failed to parse latest/release", e);
        }

        return metadata;
    }

    private String getGroupId(Element root) {
        return root.getElementsByTagName("groupId").item(0).getTextContent().trim();
    }

    private String getArtifactId(Element root) {
        return root.getElementsByTagName("artifactId").item(0).getTextContent().trim();
    }

    private LocalDateTime getLastUpdated(Element root) {
        String ts = root.getElementsByTagName("lastUpdated").item(0).getTextContent().trim();
        logger.debug("{} - Last updated: {}",getClass().getName(), ts);
        return LocalDateTime.parse(ts, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String getLatest(Element root) throws XPathExpressionException {
        XPathExpression expression = buildXPath("/metadata/versioning/latest/text()");
        return (String) expression.evaluate(root, XPathConstants.STRING);
    }
    private String getRelease(Element root) throws XPathExpressionException {
        XPathExpression expression = buildXPath("/metadata/versioning/release/text()");
        return (String) expression.evaluate(root, XPathConstants.STRING);
    }

    private Path getMetadataFilePath(ArtifactEntity entity) {
        return Paths.get(cacheLocation, ArtifactSupport.getArtifactMetaDataFile(entity));
    }
}
