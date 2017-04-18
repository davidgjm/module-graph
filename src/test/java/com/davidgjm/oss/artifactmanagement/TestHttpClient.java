package com.davidgjm.oss.artifactmanagement;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;

/**
 * Created by david on 2017/4/18.
 */
public class TestHttpClient {
    private final Logger logger= LoggerFactory.getLogger(getClass());
    private CloseableHttpClient client;

    @Before
    public void setUp() throws Exception {
        client = HttpClients.createDefault();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void testDownloadPom() throws Exception {
        String url = "http://repo1.maven.org/maven2/com/netflix/hystrix/hystrix-core/1.5.9/hystrix-core-1.5.9.pom";
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = client.execute(request);
        logger.info("status: {}", response.getStatusLine());

        HttpEntity entity = response.getEntity();
        FileOutputStream fos = new FileOutputStream("hystrix-core-1.5.9.pom");
        entity.writeTo(fos);
        fos.close();
        response.close();
    }
}
