package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.domain.Artifact;
import com.davidgjm.oss.maven.domain.Module;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by david on 2017/3/6.
 */
public interface ModuleGraphAnalyzer {

    Module analyze(@NotNull @Valid Artifact artifact);
}
