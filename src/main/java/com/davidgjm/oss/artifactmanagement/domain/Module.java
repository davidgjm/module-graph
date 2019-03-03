package com.davidgjm.oss.artifactmanagement.domain;

import com.davidgjm.oss.artifactmanagement.ArtifactEntity;
import com.davidgjm.oss.artifactmanagement.GraphNode;
import com.davidgjm.oss.artifactmanagement.support.ArtifactSupport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.*;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by david on 2017/3/1.
 */
@NodeEntity
public class Module implements GraphNode,ArtifactEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Index
    private String groupId;

    @Index
    private String artifactId;

    private String version;

    @NotBlank
    @Index
    private String name;

    @JsonIgnore
    @Index(unique = true, primary = true)
    private String compositeId;

    @Valid
    @Relationship(type = "PARENT")
    private Module parent;

    @Valid
    @Relationship(type = "DEPENDS_ON")
    private Set<Module> dependencies = new HashSet<>();

    public Module(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        refreshCompositeId();
    }

    public Module(String groupId, String artifactId) {
        this(groupId, artifactId, null);
    }

    public Module() {
    }

    public Long getId() {
        return id;
    }

    public String getCompositeId() {
        return compositeId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
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

    public Set<Module> getDependencies() {
        return dependencies;
    }

    /**
     * Setter for property 'dependencies'.
     *
     * @param dependencies Value to set for property 'dependencies'.
     */
    public void setDependencies(Set<Module> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(Module module) {
        Objects.requireNonNull(module);
        if (dependencies == null) {
            dependencies = new HashSet<>();
        }
        if(!dependencies.contains(module))
        this.dependencies.add(module);
    }

    public void refreshCompositeId() {
        if (!StringUtils.hasText(groupId)) {
            throw new IllegalStateException("Group id is required!");
        }
        if (!StringUtils.hasText(artifactId)) {
            throw new IllegalStateException("Artifact id is required!");
        }

        this.compositeId = ArtifactSupport.getCompositeId(this);
    }

    public Artifact toArtifact() {
        Artifact a= new Artifact(groupId, artifactId, version);
        a.setName(name);
        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Module module = (Module) o;

        if (!getGroupId().equals(module.getGroupId())) return false;
        if (!getArtifactId().equals(module.getArtifactId())) return false;
        return getVersion() != null ? getVersion().equals(module.getVersion()) : module.getVersion() == null;
    }

    @Override
    public int hashCode() {
        int result = getGroupId().hashCode();
        result = 31 * result + getArtifactId().hashCode();
        result = 31 * result + (getVersion() != null ? getVersion().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Module{");
        sb.append("groupId='").append(groupId).append('\'');
        sb.append(", artifactId='").append(artifactId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
