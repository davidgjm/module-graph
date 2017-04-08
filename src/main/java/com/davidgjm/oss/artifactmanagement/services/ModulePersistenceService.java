package com.davidgjm.oss.artifactmanagement.services;

import com.davidgjm.oss.artifactmanagement.domain.Module;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * Created by david on 2017/3/1.
 */
public interface ModulePersistenceService {

    Module save(@NotNull @Valid Module module);

    void save(@NotNull @Valid List<Module> modules);

    void delete(@NotNull @Valid Module module);

    Collection<Module> findAll();
}
