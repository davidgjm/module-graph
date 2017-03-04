package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.domain.Artifact;
import com.davidgjm.oss.maven.domain.MavenModuleCacheItem;
import com.davidgjm.oss.maven.domain.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by david on 2017/3/4.
 */
@Service
@Lazy
public class ModuleCacheServiceImpl implements ModuleCacheService{
    private static final String CACHE_FILE_NAME = "module-cache.yml";
    private final Logger logger= LoggerFactory.getLogger(getClass());
    private final Path cacheFile;
    private final Map<String, MavenModuleCacheItem> cache = new ConcurrentHashMap<>();

    public ModuleCacheServiceImpl() {
        cacheFile = Paths.get(System.getProperty("user.home"), ".model-graph", CACHE_FILE_NAME);
    }

    @PostConstruct
    private void doInit() throws IOException, URISyntaxException {
        logger.debug("{} - Cache file: {}",getClass().getName(), cacheFile);
        if (Files.notExists(cacheFile)) {
            logger.debug("{} - Cache file does not exist. Creating: [{}]",getClass().getName(), cacheFile);
            try {
                Files.createDirectories(cacheFile.getParent());
                Files.createFile(cacheFile);
            } catch (IOException e) {
                logger.error("Failed to create cache file.",e);
                throw new RuntimeException(e);
            }
        }

        /*
        try to load existing cache first.
        If the cache file does not exist, create a new one. Otherwise, load the content into cache.
         */
        Yaml yaml = new Yaml();
        Map<String, MavenModuleCacheItem> cached = (Map<String, MavenModuleCacheItem>) yaml.load(Files.newInputStream(cacheFile));
        if (cached != null) {
            cache.putAll(cached);
        }
        logger.info("{} items loaded from cache", cache.size());
    }


    @Override
    public MavenModuleCacheItem save(Module module) {
        Objects.requireNonNull(module);
        MavenModuleCacheItem cacheItem = new MavenModuleCacheItem(module.toArtifact());
        cacheItem.setParent(module.getParent().toArtifact());
        cacheItem.setDependencies(module.getDependencies().stream()
            .map(Module::toArtifact)
            .collect(Collectors.toList()));
        doPutCacheItem(cacheItem);
        return cacheItem;
    }

    private String getKey(Artifact artifact) {
        StringBuilder keyBuilder = new StringBuilder(String.format("%s:%s",
                artifact.getGroupId(),
                artifact.getArtifactId()));
        if (StringUtils.hasText(artifact.getVersion())) {
            keyBuilder.append(":").append(artifact.getVersion());
        }
        return keyBuilder.toString();
    }

    private void doPutCacheItem(MavenModuleCacheItem cacheItem) {
        Artifact project = cacheItem.getProject();
        String key = getKey(project);
        cache.put(key, cacheItem);
        writeToFile();
    }


    private void writeToFile() {
        logger.info("Writing cache to file: {}", cacheFile);
        logger.info("Current cache size: {}",cache.size());
        Yaml yaml = new Yaml();
        try {
            yaml.dump(cache, Files.newBufferedWriter(cacheFile));
        } catch (IOException e) {
            logger.error("Error when writing cache",e);
        }
    }

    @Override
    public void remove(Module module) {
        Objects.requireNonNull(module);
        Artifact artifact = module.toArtifact();
        logger.debug("{} - Removing artifact from cache: {}",getClass().getName(), artifact);
        cache.remove(getKey(artifact));
        writeToFile();
    }

    @Override
    public void clear() {
        cache.clear();
        writeToFile();
    }

    @Override
    public List<MavenModuleCacheItem> findAll() {
        return cache.values().parallelStream().collect(Collectors.toList());
    }

    @Override
    public Optional<Module> find(Artifact artifact) {
        Objects.requireNonNull(artifact);
        String key = getKey(artifact);
        if (!cache.containsKey(key)) {
            logger.warn("Artifact not found in cache: {}",key);
            return Optional.empty();
        }
        MavenModuleCacheItem cacheItem = cache.get(key);
        if (cacheItem == null) {
            return Optional.empty();
        }
        return Optional.of(cacheItem.toModule());
    }
}
