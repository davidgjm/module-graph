package com.davidgjm.oss.maven.providers;

import com.davidgjm.oss.maven.domain.Artifact;
import com.davidgjm.oss.maven.domain.RemotePomFile;

import java.net.URL;
import java.util.function.Supplier;

/**
 * Created by david on 2017/3/4.
 */
public interface RemoteRepositoryProvider extends Supplier<URL> {
    /**
     * Gets the base URL to the implementing provider.
     * <p>
     *     For example, <code><a href="http://repo1.maven.org/">http://repo1.maven.org/</a></code>
     * </p>
     * @return The base URL to the underlying provider
     */
    URL get();

    /**
     * Calculates url to the specified artifact.
     * @param artifact The artifact to be searched.
     * @return The URL to the corresponding artifact.
     */
    RemotePomFile getRemoteArtifactPom(Artifact artifact);
}