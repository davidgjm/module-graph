package com.davidgjm.oss.artifactmanagement.services;

import com.davidgjm.oss.artifactmanagement.domain.Module;
import com.davidgjm.oss.artifactmanagement.repository.ModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by david on 2017/3/1.
 */
@Service
public class ModulePersistenceServiceImpl implements ModulePersistenceService {
    private final Logger logger= LoggerFactory.getLogger(getClass());
    private ModuleRepository repository;

    @Autowired
    public void setRepository(ModuleRepository repository) {
        this.repository = repository;
    }


    @Override
    public Module save(@NotNull @Valid Module module) {
        saveGraphTree(module);
        return saveSingle(module);
    }

    @Transactional
    private Module saveSingle(Module module) {
        module.refreshCompositeId();
        module.setName(module.getArtifactId());
        return repository.save(module);
    }

    private void saveGraphTree(Module module) {
        //save itself
        saveSingle(module);

        //save parent
        saveAncestors(module);

        //save dependencies
        saveDependencies(module);
    }

    private void saveAncestors(Module module) {
        Module parent = module.getParent();
        if (parent == null) return;

        saveSingle(parent);
        saveAncestors(parent);
    }

    private void saveDependencies(Module module) {
        Set<Module> dependencies = module.getDependencies();
        if (dependencies==null||dependencies.isEmpty()) return;
        dependencies.parallelStream().forEach(m ->{
            saveSingle(m);
            saveDependencies(m);
        });
    }

    @Override
    public void save(@NotNull @Valid List<Module> modules) {
        if (modules.isEmpty()) return;
        modules.parallelStream().forEach(this::save);
    }

    @Override
    public void delete(@NotNull @Valid Module module) {
        logger.debug("{} - Deleting module [{}]",getClass().getName(), module);
        repository.delete(module);
    }

    @Override
    public Collection<Module> findAll() {
        Iterable<Module> iterable= repository.findAll();
        Collection<Module> modules = new ArrayList<>();
        iterable.forEach(modules::add);
        return modules;
    }
}
