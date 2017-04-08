package com.davidgjm.oss.artifactmanagement.web;

import com.davidgjm.oss.artifactmanagement.domain.Module;
import com.davidgjm.oss.artifactmanagement.services.ModuleGraphAnalyzer;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * Created by david on 2017/3/14.
 */
@RestController
@RequestMapping("/modules/analyzer")
public class ModuleAnalysisController {
    private final Logger logger= LoggerFactory.getLogger(getClass());

    private final ModuleGraphAnalyzer analyzer;

    public ModuleAnalysisController(ModuleGraphAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @GetMapping
    public Module analyze(@RequestParam("groupId") @NotNull @NotBlank String groupId,
                          @RequestParam("artifactId") @NotNull @NotBlank String artifactId,
                          @RequestParam("version") @NotBlank String version) {
        Module artifact = new Module(groupId, artifactId, version);
        logger.info("Analyzing artifact [{}]",artifact);
        Module module= analyzer.analyze(artifact);
        return module;
    }
}
