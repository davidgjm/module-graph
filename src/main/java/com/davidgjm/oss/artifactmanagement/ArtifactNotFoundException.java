package com.davidgjm.oss.artifactmanagement;

/**
 * Created by david on 2017/4/8.
 */
public class ArtifactNotFoundException extends IllegalStateException{
    public ArtifactNotFoundException() {
        super();
    }

    public ArtifactNotFoundException(String s) {
        super(s);
    }

    public ArtifactNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtifactNotFoundException(Throwable cause) {
        super(cause);
    }

    public ArtifactNotFoundException(String groupId, String artifactId, String version) {
        this(String.format("Artifact not found. groupId=%s, artifactId=%s, version=%s", groupId, artifactId, version));
    }
    public ArtifactNotFoundException(ArtifactEntity artifact) {
        this(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }
}
