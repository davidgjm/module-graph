package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.domain.Module;
import com.davidgjm.oss.maven.repository.ModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by david on 2017/3/1.
 */
@Service
public class ModuleServiceImpl implements ModuleService {
    private final Logger logger= LoggerFactory.getLogger(getClass());
    private ModuleRepository repository;

    @Autowired
    public void setRepository(ModuleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Module save(@NotNull @Valid Module module) {
        module.refreshCompositeId();
        return repository.save(module);
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
