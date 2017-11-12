package se.seppa.plugin.latestdependency;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class BestReader {
    private static final ObjectMapper OBJECT_MAPPER = new XmlMapper();
    private Log log;

    public BestReader(Log log) {
        this.log = log;
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Stream<Dependency> convertAndGetDependenciesFromPomFile(File pomFile) throws MojoFailureException {
        return getDependenciesFromPomFile(convertToModelBase(pomFile));
    }

    private Model convertToModelBase(File pomFile) throws MojoFailureException {
        try {
            return OBJECT_MAPPER.readValue(pomFile, Model.class);
        } catch (IOException e) {
            log.error("Could not parse pom-file", e);
            throw new MojoFailureException("Could not parse pom-file", e);
        }
    }

    private Stream<Dependency> getDependenciesFromPomFile(Model modelBase) {
        return modelBase.getDependencies()
                .stream()
                .map(dependency -> getDependency(modelBase, dependency));
    }

    private Dependency getDependency(Model model, Dependency dependency) {
        if(dependency.getVersion().contains("${project.parent.version}")) {
            dependency.setVersion(model.getParent().getVersion());
        }
        else if(dependency.getVersion().contains("${project.version}")) {
            dependency.setVersion(model.getVersion());
        }

        return dependency;
    }

}
