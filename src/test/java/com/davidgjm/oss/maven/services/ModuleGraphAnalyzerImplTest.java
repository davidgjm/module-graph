package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.BaseTest;
import com.davidgjm.oss.maven.domain.Artifact;
import com.davidgjm.oss.maven.domain.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

/**
 * Created by david on 2017/3/6.
 */
public class ModuleGraphAnalyzerImplTest extends BaseTest {

    @Autowired
    private ModuleGraphAnalyzer analyzer;


    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void analyze_noParentAndNoDeps() throws Exception {
        Artifact artifact = new Artifact("org.yaml","snakeyaml","1.17");
        Module module = analyzer.analyze(artifact);
        assertNotNull(module);
        System.out.println(module);
    }

    @Test
    public void analyze_ParentAndNoDeps() throws Exception {
        Artifact artifact = new Artifact("com.google.guava","guava","20.0");
        Module module = analyzer.analyze(artifact);
        assertNotNull(module);
        System.out.println(module);
    }

}