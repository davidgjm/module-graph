package com.davidgjm.oss.maven.services;

import com.davidgjm.oss.maven.domain.Module;

import java.io.IOException;
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

    Module parse(Path pomFile) throws IOException;


}
