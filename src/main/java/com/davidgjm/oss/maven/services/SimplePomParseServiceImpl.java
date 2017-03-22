package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.ArtifactEntity;
import com.davidgjm.oss.maven.configuration.AppConfiguration;
import com.davidgjm.oss.maven.domain.Module;
import com.davidgjm.oss.maven.domain.RemotePomFile;
import com.davidgjm.oss.maven.providers.RemoteRepositoryProvider;
import com.davidgjm.oss.maven.support.ArtifactSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
@Lazy
public class SimplePomParseServiceImpl implements PomParseService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
    private final XPathFactory xPathFactory = XPathFactory.newInstance();
    private final Path localPomCacheDirectory;

    private RemoteRepositoryProvider remoteRepositoryProvider;
    private final AppConfiguration configuration;

    public SimplePomParseServiceImpl(AppConfiguration configuration) {
        this.configuration = configuration;
        localPomCacheDirectory = Paths.get(configuration.getDataDirectory().toString(), "pom-cache");
    }

    @Autowired
    @Lazy
    public void setRemoteRepositoryProvider(RemoteRepositoryProvider remoteRepositoryProvider) {
        this.remoteRepositoryProvider = remoteRepositoryProvider;
    }


    @Override
    public Module parse(Module artifact) {
        Objects.requireNonNull(artifact);
        return doParseArtifact(artifact);
    }

    private Module doParseArtifact(ArtifactEntity artifact) {
        ArtifactSupport.validate(artifact);
       /*
         * Here is how a raw artifact is parsed.
         * A raw artifact is an artifact parsed out of parent or dependency element.
         * The name attribute and even version attribute is missing. This method will try to resolve the missing attributes.
         */

        /*
         * How it works:
         * 1. It will look for the pom file in the local repository or storage.
         * 2. It will download the pom from public repository if it's not found locally.
         * 3. Do parsing work
         * 4. Store the pom for future reuse
         */

        RemotePomFile remotePomFile = remoteRepositoryProvider.getRemoteArtifactPom(artifact);

        /*
         * The cached version will be checked first. If the pom is not cached, the remote file will be retrieved.
         */
        if (!isCached(remotePomFile)) {
            logger.info("{} - Pom file is not cached for {}:{}",getClass().getName(), artifact.getGroupId(), artifact.getArtifactId());
            //The file is not cached. Reading remotely.
            try {
                InputStream inputStream = fetchRemotePomContent(remotePomFile);
                BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                saveRemotePom(remotePomFile, reader.lines().collect(Collectors.toList()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return doParsePom(getCachedPomFile(remotePomFile));
    }




    private InputStream fetchRemotePomContent(RemotePomFile pomFile) throws IOException {
        URL pomUrl = pomFile.toAbsoluteUrl();
        logger.info("Fetching remote pom [{}]", pomUrl);
        return pomUrl.openStream();
    }

    private Path getCachedPomFile(RemotePomFile pomFile) {
        return Paths.get(localPomCacheDirectory.toString(), pomFile.getPomPath());
    }

    private boolean isCached(RemotePomFile pomFile) {
        return Files.exists(getCachedPomFile(pomFile));
    }

    private void saveRemotePom(RemotePomFile pomFile, List<String> lines) throws IOException {
        Path cachedPomFile = getCachedPomFile(pomFile);
        logger.debug("{} - Caching remote pom to [{}]",getClass().getName(), cachedPomFile);
        Files.createDirectories(cachedPomFile.getParent());
        Files.write(cachedPomFile, lines);
    }

    @Override
    public Module parse(Path pomFile) {
        logger.debug("{} - Parsing pom file: {}",getClass().getName(), pomFile);
        return doParsePom(pomFile);
    }


    private Module doParsePom(Path file) {
        validateXmlFile(file);
        Document document = null;
        try {
            document= getDocumentBuilder().parse(file.toFile());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Element projectElement = document.getDocumentElement();

        Module parent = getParent(document);

        //sets artifact id
        String artifactId = getArtifactId(projectElement);
        Optional<String> groupIdOptional = getGroupId(projectElement);
        String groupId = null;
        if (groupIdOptional.isPresent()) {
            groupId = groupIdOptional.get();
        } else if (parent == null) {
            logger.error("Group id is missing and nowhere to inherit. [{}]!", artifactId);
            throw new IllegalStateException("Failed to get groupId for " + artifactId);
        } else {
            groupId = parent.getGroupId();
        }


        Module module = new Module(groupId, artifactId);
        Optional<String> versionOptional = getVersion(projectElement);
        versionOptional.ifPresent(module::setVersion);
        //if version is missing from current artifact, it usually inherits from its parent
        if (parent != null) {
            module.setVersion(parent.getVersion());
        }

        //finds artifact name
        setArtifactName(projectElement, module);
        module.setParent(parent);

        //set dependencies
        module.getDependencies().addAll(loadDependencies(document));
        return module;
    }

    private  <T extends ArtifactEntity> void setArtifactName(Element container, T module) {
        Optional<String> nameOptional = getChildElementText(container, "name");
        nameOptional.ifPresent(module::setName);
    }

    private Optional<String> getGroupId(Element node) {
        return getChildElementText(node, "groupId");
    }
    private String getArtifactId(Element node) {
        Optional<String> optional = getChildElementText(node, "artifactId");
        return optional.orElseThrow(() -> new RuntimeException("No artifact id detected for element: " + node.getTagName()));
    }

    /**
     * Finds the first descendant element text for the given element.
     * @param node The node to be searched from
     * @param tagName The tag name to be matched
     * @return Optional text value.
     */
    private Optional<String> getChildElementText(Element node, String tagName) {
        NodeList nodeList = node.getElementsByTagName(tagName);
        if (nodeList == null || nodeList.getLength() == 0) {
            return Optional.empty();
        }

        nodeList = node.getChildNodes();
        int childNodeLen = node.getChildNodes().getLength();
        for (int i = 0; i < childNodeLen; i++) {
            Node n=nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) n;
                if (element.getTagName().equals(tagName))
                    return Optional.of(element.getTextContent().trim());
            }
        }
        return Optional.empty();
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

    private Module getParent(Document document) {
        NodeList parentList = document.getElementsByTagName("parent");
        if (parentList.getLength() == 0) {
            logger.warn("The project does not declare parent project. Super POM is used." );
            return null;
        }
        Element parentElement = (Element) parentList.item(0);
        return parseArtifact(parentElement);
    }

    private Module parseArtifact(Element node) {
        Module module = new Module();
        Optional<String> groupOptional = getGroupId(node);
        groupOptional.ifPresent(module::setGroupId);
        module.setArtifactId(getArtifactId(node).trim());
        Optional<String> optional = getVersion(node);
        optional.ifPresent(module::setVersion);

        //sets name
        setArtifactName(node, module );
        return module;
    }

    private List<Module> loadDependencies(Document document) {
        List<Module> dependencies = new ArrayList<>();

        NodeList depList=document.getElementsByTagName("dependencies");
        if (depList.getLength() == 0) {
            logger.warn("No dependencies defined!" );
            return dependencies;
        }
        NodeList nodeList = parseByXpath(document, "/project/dependencies/dependency");

        int length = nodeList.getLength();
        logger.trace("Found {} dependency elements.",length );

        for (int i = 0; i < length; i++) {
            Element node = (Element) nodeList.item(i);
            //check scope and type
            NodeList scopeList = node.getElementsByTagName("scope");
            if (scopeList.getLength()==1 && scopeList.item(0).getTextContent().trim().equalsIgnoreCase("test")) {
                continue;
            }
            Module artifact = parseArtifact(node);
            dependencies.add(artifact);
        }

        return dependencies;
    }

    private void validateXmlFile(Path file) {
        Objects.requireNonNull(file);
        if (Files.notExists(file)) {
            logger.error("File does not exist: {}",file );
            throw new RuntimeException(new NoSuchFileException(file.toString()));
        }
        if (Files.isDirectory(file)) {
            throw new IllegalStateException("An xml file is expected! "+file);
        }
        logger.debug("Provided file: [{}]",file );
        if (!file.getFileName().endsWith("pom.xml") && !file.getFileName().toString().endsWith(".pom")) {
            throw new IllegalArgumentException("The file is not a Maven pom.xml!");
        }
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        return factory.newDocumentBuilder();
    }
}
