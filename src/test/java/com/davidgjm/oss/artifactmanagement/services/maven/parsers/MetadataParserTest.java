package com.davidgjm.oss.artifactmanagement.services.maven.parsers;

import com.davidgjm.oss.artifactmanagement.domain.Artifact;
import com.davidgjm.oss.artifactmanagement.domain.MavenMetadata;
import com.davidgjm.oss.artifactmanagement.maven.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

/**
 * Created by david on 2017/4/18.
 */
public class MetadataParserTest extends BaseTest {
    @Autowired
    private MetadataParser parser;
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void parse() throws Exception {
        Artifact artifact = new Artifact("com.netflix.hystrix", "hystrix-core");
        MavenMetadata metadata = parser.parse(artifact);
        assertNotNull(metadata);
        System.out.println(metadata);
    }

}