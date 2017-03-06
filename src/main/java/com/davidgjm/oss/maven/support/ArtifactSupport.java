package com.davidgjm.oss.maven.support;

import com.davidgjm.oss.maven.ArtifactEntity;
import com.davidgjm.oss.maven.domain.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created by david on 2017/3/4.
 */
public final class ArtifactSupport {
    private static final Logger LOGGER= LoggerFactory.getLogger(ArtifactSupport.class);

    private ArtifactSupport(){}

    /**
     * Gets the remote pom file name for the proivded artifact and version
     * @param artifactId The artifactId
     * @param version version number
     * @return A file name representing the artifact's pom like <b><code>snakeyaml-1.18.pom</code></b>.
     */
    public static String getArtifactPomFileName(String artifactId, String version) {
        return String.format("%s-%s.pom", artifactId, version);
    }

    public static String getRelativeUrlFragment(String groupId, String artifactId) {
        validateGroupId(groupId);
        validateArtifactId(artifactId);

        return String.format("%s/%s",
                groupId.replace('.','/'),
                artifactId
                );
    }
    /**
     * Computes the POM url fragment for the specified artifact.
     * <p>
     *     Public repositories seem follow the convention about artifact urls.
     *     Basically, the artifact url follows the following pattern.
     *     <br>
     *         <b>${groupId}/${artifactId}/${version}/${artifactId}-${version}.pom</b>
     * </p>
     * <p>
     *     Where:
     *     <ul>
     *         <li><code>${groupId}</code> - Required. It is the group id for the artifact. The dot symbols are replaced with forward slashes.
     *          For example, the groupId <code>org.yaml</code> will be <b>org/yaml</b>.</li>
     *         <li><code>${artifactId}</code> - Required. It is the artifact like <b>snakeyaml</b></li>
     *         <li><code>${version}</code> - Optional. It is the version number of the artifact. </li>
     *     </ul>
     * </p>
     * @param groupId The artifact to be searched.
     * @param artifactId The artifact to be searched.
     * @param groupId The artifact to be searched.
     * @param version
     * @return The relative POM url for the artifact.
     */
    public static String getRelativeUrlFragment(String groupId, String artifactId, String version) {
        if (!StringUtils.hasText(version)) {
            throw new IllegalArgumentException("version is required!");
        }
        String url = getRelativeUrlFragment(groupId, artifactId);
        return String.format("%s/%s/%s",
                url,
                version,
                getArtifactPomFileName(artifactId, version)
                );
    }

    public static <T extends ArtifactEntity> String getCompositeId(T artifact) {
        Objects.requireNonNull(artifact);
        StringBuilder keyBuilder = new StringBuilder(String.format("%s:%s",
                artifact.getGroupId(),
                artifact.getArtifactId()));
        if (StringUtils.hasText(artifact.getVersion())) {
            keyBuilder.append(":").append(artifact.getVersion());
        }
        return keyBuilder.toString();
    }

    private static void validateGroupId(String groupId) {
        if (!StringUtils.hasText(groupId)) {
            throw new IllegalArgumentException("GroupId is required!");
        }
    }

    private static void validateArtifactId(String artifactId) {
        if (!StringUtils.hasText(artifactId)) {
            throw new IllegalArgumentException("GroupId is required!");
        }
    }

    public static void validate(Artifact artifact) {
        Objects.requireNonNull(artifact);
        validateGroupId(artifact.getGroupId());
        validateArtifactId(artifact.getArtifactId());
    }
}
