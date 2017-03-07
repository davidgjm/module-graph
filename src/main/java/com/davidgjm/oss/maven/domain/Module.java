package com.davidgjm.oss.maven.domain;

import com.davidgjm.oss.maven.ArtifactEntity;
import com.davidgjm.oss.maven.GraphNode;
import com.davidgjm.oss.maven.support.ArtifactSupport;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by david on 2017/3/1.
 */
@NodeEntity
public class Module implements GraphNode,ArtifactEntity {

    @GraphId
    private Long id;
    @Index
    private String groupId;
    @Index
    private String artifactId;
    private String version;
    @Index
    private String name;

    @Index(unique = true, primary = true)
    private String compositeId;

    @Relationship(type = "PARENT")
    private Module parent;

    @Relationship(type = "DEPEND_ON")
    private final List<Module> dependencies = new ArrayList<>();

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

    public List<Module> getDependencies() {
        return dependencies;
    }

    public void addDependency(Module module) {
        Objects.requireNonNull(module);
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
