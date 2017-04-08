package com.davidgjm.oss.artifactmanagement.configuration;

import com.davidgjm.oss.artifactmanagement.domain.ArtifactRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * <div>
 * Created with IntelliJ IDEA.
 * User: Jian-Min Gao <br>
 * Date: 2017/4/6 <br>
 * Time: 9:37 <br>
 * </div>
 */
@ConfigurationProperties("application.configuration.artifact")
public class ArtifactRepositoryProperties {
    private final List<ArtifactRepository> repositories = new ArrayList<>();

    /**
     * Getter for property 'repositories'.
     *
     * @return Value for property 'repositories'.
     */
    public List<ArtifactRepository> getRepositories() {
        return repositories;
    }
}
