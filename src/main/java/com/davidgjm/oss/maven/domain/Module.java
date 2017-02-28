package com.davidgjm.oss.maven.domain;

import com.davidgjm.oss.maven.GraphNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by david on 2017/3/1.
 */
@NodeEntity
public class Module implements GraphNode {

    @GraphId
    private Long id;
    private String group;
    private String artifact;
    private String version;
    private String name;

    @Relationship(type = "PARENT")
    private Module parent;

    @Relationship(type = "DEPEND_ON")
    private final List<Module> dependencies = new ArrayList<>();

    public Module(String group, String artifact, String version) {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
    }

    public Module() {
    }

    public Long getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Module getParent() {
        return parent;
    }

    public void setParent(Module parent) {
        this.parent = parent;
    }

    public List<Module> getDependencies() {
        return dependencies;
    }

    public void addDependency(Module module) {
        Objects.requireNonNull(module);
        if(!dependencies.contains(module))
        this.dependencies.add(module);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Module module = (Module) o;

        if (!getGroup().equals(module.getGroup())) return false;
        if (!getArtifact().equals(module.getArtifact())) return false;
        return getVersion() != null ? getVersion().equals(module.getVersion()) : module.getVersion() == null;
    }

    @Override
    public int hashCode() {
        int result = getGroup().hashCode();
        result = 31 * result + getArtifact().hashCode();
        result = 31 * result + (getVersion() != null ? getVersion().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Module{");
        sb.append("group='").append(group).append('\'');
        sb.append(", artifact='").append(artifact).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
