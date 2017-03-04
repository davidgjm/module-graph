package com.davidgjm.oss.maven.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by david on 2017/3/4.
 */
public class MavenModuleCacheItem implements Serializable{
    private Artifact project;
    private Artifact parent;
    private List<Artifact> dependencies = new ArrayList<>();

    public MavenModuleCacheItem(Artifact project) {
        this.project = project;
    }

    public MavenModuleCacheItem() {
    }

    public Artifact getProject() {
        return project;
    }

    public void setProject(Artifact project) {
        this.project = project;
    }

    public Artifact getParent() {
        return parent;
    }

    public void setParent(Artifact parent) {
        this.parent = parent;
    }

    public List<Artifact> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Artifact> dependencies) {
        this.dependencies = dependencies;
    }

    public Module toModule() {
        Module module = new Module(project.getGroupId(), project.getArtifactId(), project.getVersion());
        if (parent!=null) {
            module.setParent(parent.toModule());
        }
        if (dependencies !=null && !dependencies.isEmpty()) {
            module.getDependencies().addAll(dependencies.stream()
                    .map(Artifact::toModule)
                    .collect(Collectors.toList()));
        }
        return module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MavenModuleCacheItem that = (MavenModuleCacheItem) o;

        if (!getProject().equals(that.getProject())) return false;
        return getParent() != null ? getParent().equals(that.getParent()) : that.getParent() == null;
    }

    @Override
    public int hashCode() {
        int result = getProject().hashCode();
        result = 31 * result + (getParent() != null ? getParent().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MavenModuleCacheItem{");
        sb.append("project=").append(project);
        sb.append(", parent=").append(parent);
        sb.append(", dependencies=").append(dependencies);
        sb.append('}');
        return sb.toString();
    }
}
