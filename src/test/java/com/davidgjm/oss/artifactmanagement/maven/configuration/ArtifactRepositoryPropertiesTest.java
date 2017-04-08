package com.davidgjm.oss.artifactmanagement.maven.configuration;

import com.davidgjm.oss.artifactmanagement.configuration.ArtifactRepositoryProperties;
import com.davidgjm.oss.artifactmanagement.maven.BaseTest;
import com.davidgjm.oss.artifactmanagement.domain.ArtifactRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * <div>
 * Created with IntelliJ IDEA.
 * User: Jian-Min Gao <br>
 * Date: 2017/4/6 <br>
 * Time: 10:15 <br>
 * </div>
 */

public class ArtifactRepositoryPropertiesTest extends BaseTest {
    @Autowired
    private ArtifactRepositoryProperties properties;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getRepositories() throws Exception {
        List<ArtifactRepository> repositories = properties.getRepositories();
        assertTrue(repositories != null && !repositories.isEmpty());
        repositories.forEach(r -> logger.debug("path: {}", r.getPath()));
    }

}
