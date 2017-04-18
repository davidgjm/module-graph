package com.davidgjm.oss.artifactmanagement.services.maven;

import com.davidgjm.oss.artifactmanagement.domain.Artifact;
import com.davidgjm.oss.artifactmanagement.maven.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

/**
 * Created by david on 2017/4/18.
 */
public class MetaFileDownloaderTest extends BaseTest {
    @Autowired
    private MetaFileDownloader downloader;
    private Artifact artifact;

    @Before
    public void setUp() throws Exception {
        artifact = new Artifact("com.netflix.hystrix", "hystrix-core");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void downloadGroupMetadata() throws Exception {
        Path metadataFile = downloader.downloadGroupMetadata(artifact);
        assertTrue(Files.exists(metadataFile) && Files.size(metadataFile) > 1);
    }

}