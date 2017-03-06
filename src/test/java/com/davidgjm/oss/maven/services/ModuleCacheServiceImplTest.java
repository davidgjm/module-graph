package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.BaseTest;
import com.davidgjm.oss.maven.domain.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Created by david on 2017/3/4.
 */
public class ModuleCacheServiceImplTest extends BaseTest {
    @Autowired
    private PomParseService pomParseService;

    @Autowired
    private ModuleCacheService cacheService;

    private List<Module> modules=new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        Path pomFile = Paths.get(System.getProperty("user.dir"), "pom.xml");
        logger.debug("{} - Current project pom: {}",getClass().getName(),  pomFile);
        Module module=pomParseService.parse(pomFile);
        if (module == null) {
            fail("Failed to load current project pom");
        }

        modules.add(module);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void save() throws Exception {
        cacheService.save(modules.get(0));

    }

    @Test
    public void remove() throws Exception {

    }

    @Test
    public void clear() throws Exception {

    }

    @Test
    public void findAll() throws Exception {

    }

    @Test
    public void find() throws Exception {

    }

}