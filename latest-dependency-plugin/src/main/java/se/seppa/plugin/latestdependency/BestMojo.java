package se.seppa.plugin.latestdependency;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import se.seppa.plugin.latestdependency.dto.Dependency;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Mojo(name = "thething", defaultPhase = LifecyclePhase.VALIDATE)
public class BestMojo extends AbstractMojo {
    @Parameter(property = "pomFile", defaultValue = "${project.file}")
    private File pomFile;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Running the plugin yo");

        logLatestDependencies(
                fetchLatestDependencies(
                        getDependenciesFromPomFile(
                                getPomFileContent(pomFile)
                        )
                )
        );
    }

    private String getPomFileContent(File pomFile) throws MojoFailureException {
        try {
            return new String(Files.readAllBytes(Paths.get(pomFile.toURI())));
        } catch (IOException e) {
            getLog().error("Could not read pom file: " + e.getMessage(), e);
            throw new MojoFailureException("Shit craycray yo", e);
        }
    }

    private List<Dependency> getDependenciesFromPomFile(String pomFileContent) {
        return Collections.emptyList();
    }

    private List<Dependency> fetchLatestDependencies(List<Dependency> dependencies) {
        final String url = "http://search.maven.org/solrsearch/select?q=g:$GROUP_ID+AND+a:$ARTIFACT_ID&rows=20&wt=json";
        final String groupIdReplacer = "GROUP_ID";
        final String artifactIdReplacer = "ARTIFACT_ID";

        return dependencies;
    }

    private void logLatestDependencies(List<Dependency> dependencies) {
        dependencies.forEach(dependency -> getLog().info("Newest dependency is: " + dependency));
    }
}
