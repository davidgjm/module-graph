package com.davidgjm.oss.maven.domain;

import com.davidgjm.oss.maven.GraphNode;
import org.neo4j.ogm.annotation.*;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by david on 2017/3/1.
 */
@NodeEntity
public class Module implements GraphNode {

    private static final String COMPOSITE_KEY_PATTERN = "{0}:{1}";

    @GraphId
    private Long id;
    @Index
    private String group;
    @Index
    private String artifact;
    private String version;
    @Index
    private String name;

    @Index(unique = true, primary = true)
    private String compositeId;

    @Relationship(type = "PARENT")
    private Module parent;

    @Relationship(type = "DEPEND_ON")
    private final List<Module> dependencies = new ArrayList<>();

    public Module(String group, String artifact, String version) {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
        refreshCompositeId();
    }

    public Module(String group, String artifact) {
        this(group, artifact, null);
    }

    public Module() {
    }

    public Long getId() {
        return id;
    }

    public String getCompositeId() {
        return compositeId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
        refreshCompositeId();
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
        refreshCompositeId();
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

    private void refreshCompositeId() {
        if (!StringUtils.hasText(group)) {
            throw new IllegalStateException("Group id is required!");
        }
        if (!StringUtils.hasText(artifact)) {
            throw new IllegalStateException("Artifact id is required!");
        }

        this.compositeId = MessageFormat.format(COMPOSITE_KEY_PATTERN, group, artifact);
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
