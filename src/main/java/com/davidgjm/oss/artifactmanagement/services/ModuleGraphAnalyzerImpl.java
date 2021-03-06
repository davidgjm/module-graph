package com.davidgjm.oss.artifactmanagement.services;

import com.davidgjm.oss.artifactmanagement.domain.Module;
import com.davidgjm.oss.artifactmanagement.services.maven.PomParseService;
import com.davidgjm.oss.artifactmanagement.support.ArtifactSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by david on 2017/3/6.
 */
@Service
public class ModuleGraphAnalyzerImpl implements ModuleGraphAnalyzer{
    private final Logger logger= LoggerFactory.getLogger(getClass());
    private final ModuleCacheService cacheService;
    private PomParseService pomParseService;
    private ModulePersistenceService modulePersistenceService;
    private final ExecutorService pool = Executors.newWorkStealingPool();

    @Autowired
    public ModuleGraphAnalyzerImpl(ModuleCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Autowired
    public void setPomParseService(PomParseService pomParseService) {
        this.pomParseService = pomParseService;
    }

    @Autowired
    public void setModuleService(ModulePersistenceService modulePersistenceService) {
        this.modulePersistenceService = modulePersistenceService;
    }

    @Override
    public Module analyze(@NotNull @Valid Module artifact) {
        Objects.requireNonNull(artifact);
        ArtifactSupport.validate(artifact);
        logger.debug("{} - Analyzing artifact [{}]...",getClass().getName(), artifact);
        Module module= doAnalyze(artifact);
        analyzeAncestors(module);

        //dependencies are checked at all times for the time being
        analyzeDependencies(module, true);
        modulePersistenceService.save(module);
        pool.submit(() ->{
            cacheService.save(module);
        });
        return module;
    }


    private Module doAnalyze(Module artifact) {
        Optional<Module> moduleOptional = cacheService.find(artifact);
        if (moduleOptional.isPresent()) {
            return moduleOptional.get();
        }

        logger.info("Artifact [{}] not cached. Retrieving from pom...", artifact.getCompositeId());
        Module parsedModule = pomParseService.parse(artifact);
        pool.submit(() -> {
            logger.debug("{} - Saving parsed module to cache first [{}]",getClass().getName(), parsedModule.getCompositeId());
            cacheService.save(parsedModule);

        });
        return parsedModule;
    }

    private void analyzeAncestors(Module module) {
        Module parent = module.getParent();
        if (parent == null) {
            logger.info("The module [{}] inherits super POM. No parent defined", module.getCompositeId());
            return;
        }

        logger.info("Analyzing parent: [{}]",parent.getCompositeId());
        parent = doAnalyze(parent);
        parent.refreshCompositeId();
        module.setParent(parent);

        analyzeDependencies(parent, true);
        analyzeAncestors(parent);
    }

    private void analyzeDependencies(Module module, boolean recursive) {
        if(module ==null)return;
        Set<Module> dependencies = module.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            logger.info("No dependencies found for module: {}", module.getCompositeId());
            return;
        }
        if (recursive) {
            logger.info("Looking into dependencies for {}",module.getCompositeId());
            dependencies.parallelStream().forEach(m -> {
                logger.info("Checking dependency module: [{}]",m.getCompositeId());
                m.refreshCompositeId();
                analyzeDependencies(m,true);
            });
        }
    }
}
