package se.seppa.plugin.latestdependency;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BestIntegration {
    private static final String URL_FORMAT = "http://search.maven.org/solrsearch/select?q=g:%s+AND+a:%s&rows=20&wt=json";

    private Log log;

    public BestIntegration(Log log) {
        this.log = log;
    }

    List<Dependency> fetchLatestDependencies(Stream<Dependency> dependencies) throws MojoFailureException {
        return dependencies
                .map(this::asd)
                .filter(dependency -> dependency.getVersion() != null)
                .collect(Collectors.toList());
    }

    private Dependency asd(Dependency dependency) {
        String url = String.format(URL_FORMAT, dependency.getGroupId(), dependency.getArtifactId());

        log.debug("Querying: " + url);

        try {
            JsonNode response = Unirest.get(url)
                    .header("Accept", "application/json;charset=UTF-8")
                    .asJson()
                    .getBody();

            log.debug(response.toString());

            Iterator<Object> iterator = response.getObject()
                    .getJSONObject("response")
                    .getJSONArray("docs")
                    .iterator();

            String latestVersion = transformIterator(iterator)
                    .map(o -> o.getString("latestVersion"))
                    .filter(s -> !dependency.getVersion().equals(s)) // Should check if later version, not just different
                    .findAny()
                    .orElse(null);

            dependency.setVersion(latestVersion);
        } catch (UnirestException e) {
            log.error(e.getMessage(), e);
        }

        return dependency;
    }

    private Stream<JSONObject> transformIterator(Iterator<Object> iterator) {
        List<JSONObject> list = new ArrayList<>();

        iterator.forEachRemaining(it -> list.add((JSONObject)it));

        return list.stream();
    }

}
