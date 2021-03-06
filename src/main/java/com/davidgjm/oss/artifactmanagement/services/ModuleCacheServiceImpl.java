package com.davidgjm.oss.artifactmanagement.services;

import com.davidgjm.oss.artifactmanagement.configuration.AppConfiguration;
import com.davidgjm.oss.artifactmanagement.domain.Artifact;
import com.davidgjm.oss.artifactmanagement.domain.Module;
import com.davidgjm.oss.artifactmanagement.support.ArtifactSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
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
    private final Map<String, Module> cache = new ConcurrentHashMap<>();
    private final AppConfiguration configuration;

    public ModuleCacheServiceImpl(AppConfiguration configuration) {
        this.configuration = configuration;
        cacheFile = Paths.get(configuration.getDataDirectory().toString(), CACHE_FILE_NAME);
    }

    @PostConstruct
    private void doInit() throws IOException, URISyntaxException {
        logger.debug("{} - Cache file: {}",getClass().getName(), cacheFile);
        setupCacheFile();

        /*
        try to load existing cache first.
        If the cache file does not exist, create a new one. Otherwise, load the content into cache.
         */
        Yaml yaml = new Yaml();
        Map<String, Module> cached = (Map<String, Module>) yaml.load(Files.newInputStream(cacheFile));
        if (cached != null) {
            cache.putAll(cached);
        }
        logger.info("{} items loaded from cache", cache.size());
    }

    private void setupCacheFile() {
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
    }

    @Override
    public void save(Module module) {
        Objects.requireNonNull(module);
        doPutCacheItem(module);
        writeToFile();
    }


    private void doPutCacheItem(Module cacheItem) {
        saveModuleItem(cacheItem);
        saveAncestors(cacheItem);
        saveDependencies(cacheItem);
    }

    private void saveModuleItem(Module module) {
        String key = ArtifactSupport.getCompositeId(module);
        cache.put(key, module);
    }

    private void saveAncestors(Module module) {
        Module parent = module.getParent();
        if (parent == null) return;

        saveModuleItem(parent);
        saveAncestors(parent);
    }

    private void saveDependencies(Module module) {
        Set<Module> dependencies = module.getDependencies();
        if (dependencies==null || dependencies.isEmpty()) return;

        dependencies.parallelStream().forEach(this::saveDependencies);
    }


    private void writeToFile() {
        logger.info("Writing cache to file: {}", cacheFile);
        logger.info("Current cache size: {}",cache.size());
        setupCacheFile();
        Yaml yaml = new Yaml();
        try {
            yaml.dump(cache, Files.newBufferedWriter(cacheFile, StandardOpenOption.TRUNCATE_EXISTING));
        } catch (IOException e) {
            logger.error("Error when writing cache",e);
        }
    }

    @Override
    public void remove(Module module) {
        Objects.requireNonNull(module);
        Artifact artifact = module.toArtifact();
        logger.debug("{} - Removing artifact from cache: {}",getClass().getName(), artifact);
        cache.remove(ArtifactSupport.getCompositeId(module));
        writeToFile();
    }

    @Override
    public void clear() {
        cache.clear();
        writeToFile();
    }

    @Override
    public List<Module> findAll() {
        return cache.values().parallelStream().collect(Collectors.toList());
    }

    @Override
    public Optional<Module> find(Module artifact) {
        Objects.requireNonNull(artifact);
        String key = ArtifactSupport.getCompositeId(artifact);
        if (!cache.containsKey(key)) {
            logger.warn("Artifact not found in cache: {}",key);
            return Optional.empty();
        }
        Module cacheItem = cache.get(key);
        if (cacheItem == null) {
            return Optional.empty();
        }
        cacheItem.refreshCompositeId();
        return Optional.of(cacheItem);
    }
}
