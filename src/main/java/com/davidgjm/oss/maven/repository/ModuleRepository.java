package com.davidgjm.oss.maven.repository;

import com.davidgjm.oss.maven.domain.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by david on 2017/3/1.
 */
@Repository
public interface ModuleRepository extends GraphRepository<Module> {
    Page<Module> findAll(Pageable pageable);
}
