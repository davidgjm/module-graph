package com.davidgjm.oss.maven.configuration;

import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by david on 2017/3/14.
 */
@Configuration
public class AppConfiguration {

    public Path getDataDirectory() {
        return Paths.get(System.getProperty("user.home"), ".model-graph");
    }
}
