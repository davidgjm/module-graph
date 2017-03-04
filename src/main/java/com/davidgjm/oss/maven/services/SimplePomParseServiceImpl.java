package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.domain.Artifact;
import com.davidgjm.oss.maven.domain.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <div>
 * Created with IntelliJ IDEA.
 * User: Jian-Min Gao <br>
 * Date: 2017/3/2 <br>
 * Time: 18:29 <br>
 * </div>
 */
@Service
public class SimplePomParseServiceImpl implements PomParseService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
    private final XPathFactory xPathFactory = XPathFactory.newInstance();

    @Override
    public Module parse(Path pomFile) {
        logger.debug("{} - Parsing pom file: {}",getClass().getName(), pomFile);
        Document document = null;
        try {
            document = doParseXml(pomFile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        if (document == null) {
            throw new IllegalStateException("Failed to parse provided file: " + pomFile);
        }

        logger.debug("{} - Parsing xml document...",getClass().getName());
        return doParseXmlDocument(document);
    }

    private Module doParseXmlDocument(Document document) {
        Element projectElement = document.getDocumentElement();

        Artifact parent = getParent(document);

        //sets artifact id
        String artifactId = getArtifactId(projectElement);
        String groupId = getGroupId(projectElement);
        if (groupId == null && parent!=null) {
            groupId = parent.getGroupId();
        }

        Module module = new Module(groupId, artifactId);
        Optional<String> versionOptional = getVersion(projectElement);
        versionOptional.ifPresent(module::setVersion);

        if (parent != null) {
            module.setParent(parent.toModule());
        }

        //set dependencies
        module.getDependencies().addAll(loadDependencies(document).stream()
                .map(Artifact::toModule)
                .collect(Collectors.toList()));
        return module;
    }

    private String getGroupId(Element node) {
        return node.getElementsByTagName("groupId").item(0).getTextContent();
    }
    private String getArtifactId(Element node) {
        return node.getElementsByTagName("artifactId").item(0).getTextContent();
    }
    private Optional<String> getVersion(Element node) {
        NodeList versionList = node.getElementsByTagName("version");
        if (versionList.getLength() ==0) {
            return Optional.empty();
        }
        return Optional.of(versionList.item(0).getTextContent().trim());
    }

    private NodeList parseByXpath(Document document, String exp) {
        XPath xPath = xPathFactory.newXPath();
        XPathExpression expression = null;
        try {
            expression = xPath.compile(exp);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

        NodeList nodeList;
        try {
            nodeList= (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
        return nodeList;
    }

    private Document doParseXml(Path file) throws ParserConfigurationException, IOException, SAXException {
        validateXmlFile(file);
        return getDocumentBuilder().parse(file.toFile());
    }

    private Artifact getParent(Document document) {
        NodeList parentList = document.getElementsByTagName("parent");
        if (parentList.getLength() == 0) {
            logger.warn("The project does not declare parent project. Super POM is used." );
            return null;
        }
        Element parentElement = (Element) parentList.item(0);
        return parseArtifact(parentElement);
    }

    private Artifact parseArtifact(Element node) {
        Artifact artifact = new Artifact();
        artifact.setGroupId(getGroupId(node).trim());
        artifact.setArtifactId(getArtifactId(node).trim());
        Optional<String> optional = getVersion(node);
        optional.ifPresent(artifact::setVersion);
        return artifact;
    }

    private List<Artifact> loadDependencies(Document document) {
        List<Artifact> dependencies = new ArrayList<>();

        NodeList depList=document.getElementsByTagName("dependencies");
        if (depList.getLength() == 0) {
            logger.warn("No dependencies defined!" );
            return dependencies;
        }
        NodeList nodeList = parseByXpath(document, "//dependencies/dependency");

        int length = nodeList.getLength();
        logger.trace("Found {} dependency elements.",length );

        for (int i = 0; i < length; i++) {
            Element node = (Element) nodeList.item(i);
            //check scope and type
            NodeList scopeList = node.getElementsByTagName("scope");
            if (scopeList.getLength()==1 && scopeList.item(0).getTextContent().trim().equalsIgnoreCase("test")) {
                continue;
            }
            Artifact artifact = parseArtifact(node);
            dependencies.add(artifact);
        }

        return dependencies;
    }

    private void validateXmlFile(Path file) throws NoSuchFileException {
        Objects.requireNonNull(file);
        if (Files.notExists(file)) {
            logger.error("File does not exist: {}",file );
            throw new NoSuchFileException(file.toString());
        }
        if (Files.isDirectory(file)) {
            throw new IllegalStateException("An xml file is expected! "+file);
        }
        logger.debug("Provided file: [{}]",file );
        if (!file.getFileName().endsWith("pom.xml") && !file.getFileName().endsWith(".pom")) {
            throw new IllegalArgumentException("The file is not a Maven pom.xml!");
        }
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        return factory.newDocumentBuilder();
    }
}
