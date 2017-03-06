package com.davidgjm.oss.maven;

/**
 * Created by david on 2017/3/6.
 */
public enum ModuleGraphAnalysisPolicy {

    /**
     * Recursively scan dependencies
     */
    SCAN_DEPENDENCY_TREE,

    /**
     * Scan upwards through parent path till reaches super pom (no parent)
     */
    SCAN_ANCESTORS
}
