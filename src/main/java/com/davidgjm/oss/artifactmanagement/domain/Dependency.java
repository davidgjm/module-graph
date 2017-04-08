package com.davidgjm.oss.artifactmanagement.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * <div>
 * Created with IntelliJ IDEA.
 * User: Jian-Min Gao <br>
 * Date: 2017/3/15 <br>
 * Time: 19:19 <br>
 * </div>
 */
@RelationshipEntity(type = "DEPENDS_ON")
public class Dependency {
    @GraphId
    private Long id;
    @StartNode
    private Module project;

    @EndNode
    private Module dependency;

    public Dependency(Module project, Module dependency) {
        this.project = project;
        this.dependency = dependency;
    }

    public Dependency() {
    }

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for property 'project'.
     *
     * @return Value for property 'project'.
     */
    public Module getProject() {
        return project;
    }

    /**
     * Setter for property 'project'.
     *
     * @param project Value to set for property 'project'.
     */
    public void setProject(Module project) {
        this.project = project;
    }

    /**
     * Getter for property 'dependency'.
     *
     * @return Value for property 'dependency'.
     */
    public Module getDependency() {
        return dependency;
    }

    /**
     * Setter for property 'dependency'.
     *
     * @param dependency Value to set for property 'dependency'.
     */
    public void setDependency(Module dependency) {
        this.dependency = dependency;
    }
}
