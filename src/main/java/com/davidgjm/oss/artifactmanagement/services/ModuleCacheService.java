package com.davidgjm.oss.artifactmanagement.services;

import com.davidgjm.oss.artifactmanagement.domain.Module;

import java.util.List;
import java.util.Optional;

/**
 * Created by david on 2017/3/4.
 */
public interface ModuleCacheService {

    void save(Module module);

    void remove(Module module);

    void clear();

    List<Module> findAll();

    /**
     * Locates the artifact from the cache.
     * <p>
     * The <code>groupId</code></code> and <code>artifactId</code> are required for the artifact while <code>version</code> is optional.
     * The first matching cache item will be retrieved if the version information is missing from the artifact.
     * </p>
     * @param artifact The artifact to be located. <code>groupId</code> and <code>artifactId</code> properties are required.
     * @return The optional object containing the matching result.
     */
    Optional<Module> find(Module artifact);
}
