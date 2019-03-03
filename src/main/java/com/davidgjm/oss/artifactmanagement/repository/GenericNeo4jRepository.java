package com.davidgjm.oss.artifactmanagement.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface GenericNeo4jRepository<T> extends Neo4jRepository<T, Long> {
    @Override
    Optional<T> findById(Long id);
    <S extends T> S save(S entity);
}
