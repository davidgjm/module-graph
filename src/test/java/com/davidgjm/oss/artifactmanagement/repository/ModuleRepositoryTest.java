package com.davidgjm.oss.artifactmanagement.repository;

import com.davidgjm.oss.artifactmanagement.maven.BaseTest;
import com.davidgjm.oss.artifactmanagement.domain.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;

/**
 * Created by david on 2017/3/1.
 */

@Transactional
public class ModuleRepositoryTest extends BaseTest{
    @Autowired
    private Session session;

    @Autowired
    private ModuleRepository repository;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        session.purgeDatabase();
    }

    @Test
    public void testSave() throws Exception {
        Module parent = new Module("org.springframework.boot","spring-boot-starter-parent","1.5.1.RELEASE");

        Module project = new Module("com.davidgjm.oss.artifactmanagement.maven", "module-graph", "0.0.1-SNAPSHOT");
        project.setParent(parent);
        repository.save(project);

        Iterable<Module> iterable=repository.findAll();
        assertTrue(iterable.iterator().hasNext());
    }
}