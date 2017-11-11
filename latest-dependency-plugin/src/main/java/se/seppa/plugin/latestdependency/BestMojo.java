package se.seppa.plugin.latestdependency;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

@Mojo(name = "thething", defaultPhase = LifecyclePhase.VALIDATE)
public class BestMojo extends AbstractMojo {
    @Parameter(property = "pomFile", defaultValue = "${project.file}")
    private File pomFile;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Running the plugin yo");

        BestReader bestReader = new BestReader(getLog());
        BestIntegration bestIntegration = new BestIntegration(getLog());

        logLatestDependencies(
                bestIntegration.fetchLatestDependencies(
                        bestReader.convertAndGetDependenciesFromPomFile(pomFile)
                )
        );
    }

    private void logLatestDependencies(List<Dependency> dependencies) {
        dependencies.forEach(dependency -> getLog().warn("Newer dependency exists" + dependency));
    }
}
