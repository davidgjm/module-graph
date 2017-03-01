package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.domain.Module;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * Created by david on 2017/3/1.
 */
public interface ModuleService {

    Module save(@NotNull @Valid Module module);

    void save(@NotNull @Valid List<Module> modules);

    void delete(@NotNull @Valid Module module);

    Collection<Module> findAll();
}
