package com.davidgjm.oss.artifactmanagement.services.maven;

import com.davidgjm.oss.artifactmanagement.domain.Artifact;
import com.davidgjm.oss.artifactmanagement.maven.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by david on 2017/4/8.
 */
public class PomCacheServiceImplTest extends BaseTest {
    @Autowired
    private PomCacheService cacheService;

    @Before
    public void setUp() throws Exception {
        if (cacheService == null) {
            fail("Failed to autowire the cache service.");
        }
    }

    @Test
    public void save() throws Exception {

    }

    @Test
    public void find() throws Exception {
        Artifact artifact = new Artifact("org.eclipse.jetty", "jetty-io", "9.4.2.v20170220");
        Path path=cacheService.find(artifact);
        assertTrue(path != null && Files.exists(path));
        Files.readAllLines(path).forEach(l -> logger.info("{}",l));
    }

    @Test(expected = IllegalStateException.class)
    public void find_missing_version() throws Exception {
        Artifact artifact = new Artifact("org.eclipse.jetty", "jetty-io");
        cacheService.find(artifact);
    }

    @Test
    public void isCached() throws Exception {
        Artifact artifact = new Artifact("org.eclipse.jetty", "jetty-io", "9.4.2.v20170220");
        boolean isCached = cacheService.isCached(artifact);
        assertTrue(isCached);
    }

    @Test
    public void isCached_missing_version() throws Exception {
        Artifact artifact = new Artifact("org.eclipse.jetty", "jetty-io");
        boolean isCached = cacheService.isCached(artifact);
        assertFalse(isCached);
    }

}