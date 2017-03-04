package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.domain.Module;

import java.nio.file.Path;

/**
 * <div>
 * Created with IntelliJ IDEA.
 * User: Jian-Min Gao <br>
 * Date: 2017/3/2 <br>
 * Time: 18:24 <br>
 * </div>
 */

public interface PomParseService {

    /**
     * Parses the specified Maven pom file.
     * Do a simple XML parsing for the specified Maven pom file without checking the inheritance tree.
     * @param pomFile The project file to be parsed. The file name is usually <code>pom.xml</code> or <code>${artifact}.pom</code>
     * @return The parsed module object.
     */
    Module parse(Path pomFile);


}
