package com.davidgjm.oss.artifactmanagement.configuration;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by david on 2017/3/14.
 */
@Configuration
@EnableConfigurationProperties(ArtifactRepositoryProperties.class)
public class AppConfiguration {

    public Path getDataDirectory() {
        return Paths.get(System.getProperty("user.home"), ".model-graph");
    }

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }
}
