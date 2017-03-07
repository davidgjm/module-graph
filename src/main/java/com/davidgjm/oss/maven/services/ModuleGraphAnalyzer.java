package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.domain.Module;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by david on 2017/3/6.
 */
public interface ModuleGraphAnalyzer {

    /**
     * Performs dependency and parent analysis for the provided artifact.
     * <p>
     *     The provided artifact may or may not contain parent or dependencies. However, the cache and even remote
     *     repositories will be checked so as to included all possible entries for inheritance tree and dependency tree.
     * </p>
     * @param artifact The artifact to be analyzed. It is a simple pojo
     * @return The analyzed result optionally containing parent and dependency information if available.
     */
    Module analyze(@NotNull @Valid Module artifact);
}
