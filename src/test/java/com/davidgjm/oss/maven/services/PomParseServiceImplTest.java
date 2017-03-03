package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.BaseTest;
import com.davidgjm.oss.maven.domain.Module;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * <div>
 * Created with IntelliJ IDEA.
 * User: Jian-Min Gao <br>
 * Date: 2017/3/3 <br>
 * Time: 9:17 <br>
 * </div>
 */
public class PomParseServiceImplTest extends BaseTest{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PomParseService service;

    private Path pomFile;

    @Before
    public void setUp() throws Exception {
        pomFile = Paths.get(System.getProperty("user.dir"), "pom.xml");
        if (Files.notExists(pomFile)) {
            logger.error("Pom not found: {}",pomFile );
            fail("Pom file does not exist!");
        }
    }

    @Test
    public void parse() throws Exception {
        Module module = service.parse(pomFile);
        assertNotNull(module);
    }

}
