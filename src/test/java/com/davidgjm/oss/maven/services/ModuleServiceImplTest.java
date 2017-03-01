package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.BaseTest;
import com.davidgjm.oss.maven.domain.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

/**
 * Created by david on 2017/3/1.
 */
public class ModuleServiceImplTest extends BaseTest {
    @Autowired
    private ModuleService service;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void save() throws Exception {
        Module parent = new Module("org.springframework.boot","spring-boot-starter-parent","1.5.1.RELEASE");

        Module project = new Module("com.davidgjm.oss.maven", "module-graph", "0.0.1-SNAPSHOT");
        project.setParent(parent);
        service.save(project);

        Long parentId=parent.getId();
        //the parent should be saved as well due to the relation
        logger.debug("{} - Parent id: {}",getClass().getName(), parentId);

        assertNotNull(parentId);
    }

    @Test
    public void save1() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }

    @Test
    public void findAll() throws Exception {

    }

}