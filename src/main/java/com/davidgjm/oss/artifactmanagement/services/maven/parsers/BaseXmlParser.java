package com.davidgjm.oss.artifactmanagement.services.maven.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by david on 2017/4/18.
 */
public abstract class BaseXmlParser {
    protected final Logger logger= LoggerFactory.getLogger(getClass());
    private final DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
    protected final XPathFactory xPathFactory = XPathFactory.newInstance();

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        return factory.newDocumentBuilder();
    }

    protected Document parseXml(Path file) {
        validateXmlFile(file);
        try {
            return getDocumentBuilder().parse(file.toFile());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected XPathExpression buildXPath(String expr) {
        XPath xPath = xPathFactory.newXPath();
        XPathExpression expression = null;
        try {
            expression = xPath.compile(expr);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
        return expression;
    }

    protected void validateXmlFile(Path file) {
        Objects.requireNonNull(file);
        if (Files.notExists(file)) {
            logger.error("File does not exist: {}",file );
            throw new RuntimeException(new NoSuchFileException(file.toString()));
        }
        if (Files.isDirectory(file)) {
            throw new IllegalStateException("An xml file is expected! "+file);
        }
        logger.debug("Provided file: [{}]",file );
        if (!file.getFileName().toString().matches("(pom\\.xml|maven-metadata\\.xml|.+\\.pom)$")) {
            throw new IllegalArgumentException("The file is not a valid xml.");
        }
    }

}
