package com.davidgjm.oss.maven.providers;

import com.davidgjm.oss.maven.BaseTest;
import com.davidgjm.oss.maven.domain.Module;
import com.davidgjm.oss.maven.domain.RemotePomFile;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by david on 2017/3/4.
 */
public class MavenCentralProviderTest extends BaseTest {
    @Autowired
    private RemoteRepositoryProvider provider;

    @Before
    public void setUp() throws Exception {
        URL baseUrl = provider.get();
        if (baseUrl == null) {
            fail("Missing base URL for "+ provider.getClass().getName());
        }
    }

    @Test
    public void getArtifactUrl() throws Exception {
        Module artifact = new Module("org.yaml", "snakeyaml");

        RemotePomFile pomFile = provider.getRemoteArtifactPom(artifact);
        assertNotNull(pomFile);
        URL pomUrl = pomFile.toAbsoluteUrl();
        InputStreamReader isr = new InputStreamReader(pomUrl.openStream(), Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(isr);
        reader.lines().forEach(System.out::println);
        reader.close();

    }

}
