package com.davidgjm.oss.artifactmanagement.services.maven;

import com.davidgjm.oss.artifactmanagement.ArtifactEntity;
import com.davidgjm.oss.artifactmanagement.configuration.AppConfiguration;
import com.davidgjm.oss.artifactmanagement.domain.Module;
import com.davidgjm.oss.artifactmanagement.domain.RemotePomFile;
import com.davidgjm.oss.artifactmanagement.maven.providers.RemoteRepositoryProvider;
import com.davidgjm.oss.artifactmanagement.services.maven.parsers.BaseXmlParser;
import com.davidgjm.oss.artifactmanagement.support.ArtifactSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
public class SimplePomParseServiceImpl extends BaseXmlParser
        implements PomParseService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
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
        Module module = new Module();
        Document document = parseXml(file);
        Element projectElement = document.getDocumentElement();

        Module parent = getParent(document);

        //sets artifact id
        resolveArtifactId(projectElement, module);

        //sets group id
        resolveGroupId(projectElement,module, Optional.ofNullable(parent));

        resolveVersion(projectElement, module);
        //if version is missing from current artifact, it usually inherits from its parent
        if (parent != null) {
            module.setVersion(parent.getVersion());
        }

        //finds artifact name
//        resolveArtifactName(projectElement, module);
        module.setName(module.getArtifactId());
        module.setParent(parent);

        //set dependencies
        module.getDependencies().addAll(loadDependencies(document));
        return module;
    }

    private <T extends ArtifactEntity> T resolveArtifactName(Element container,T module) {
        Optional<String> nameOptional = getChildElementText(container, "name");
        nameOptional.ifPresent(module::setName);
        return module;
    }

    private <T extends ArtifactEntity> T resolveGroupId(Element node,T module, Optional<T> parentOptional) {
        Optional<String> groupIdOptional = getChildElementText(node, "groupId");
        if (!parentOptional.isPresent() && !groupIdOptional.isPresent()) {//throws exception when no way to get group id
            logger.error("Group id is missing and nowhere to inherit. [{}]!", module);
            throw new IllegalStateException("Failed to get groupId for " + module.getArtifactId());
        }
        if (groupIdOptional.isPresent()) {
            module.setGroupId(groupIdOptional.get());
            return module;
        } else parentOptional.ifPresent(t -> module.setGroupId(t.getGroupId()));
        return module;
    }

    private <T extends ArtifactEntity> T resolveArtifactId(Element node, T module) {
        Optional<String> optional = getChildElementText(node, "artifactId");
        module.setArtifactId(optional.orElseThrow(() -> new RuntimeException("No artifact id detected for element: " + node.getTagName())));
        return module;
    }

    private <T extends ArtifactEntity> T resolveVersion(Element node, T module) {
        NodeList versionList = node.getElementsByTagName("version");
        if (versionList.getLength() ==0) {
            logger.warn("No version element present for module: [{}]", module );
            return module;
        }
        String version = versionList.item(0).getTextContent().trim();
        logger.debug("Parsed artifact version: [{}]",version );

        module.setVersion(version);
        return module;
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


    private NodeList parseByXpath(Document document, String exp) {
        XPathExpression expression = buildXPath(exp);
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

    /**
     * Parses a single artifact. An artifact can be a project, parent, dependency or even plugin.
     * @param node The XML element in the pom.xml
     * @return The artifact parsed out of this xml element.
     */
    private Module parseArtifact(Element node) {
        Module module = new Module();
        resolveGroupId(node, module, Optional.empty());

        resolveArtifactId(node, module);
        resolveVersion(node, module);

        //sets name
//        resolveArtifactName(node, module );
        module.setName(module.getArtifactId());
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

}
