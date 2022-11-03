package io.github.kglowins.shifts.services;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.google.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Singleton
public class VersionInfoProvider {

    private static final String VERSION_INFO_RESOURCE = "/build.properties";

    private static final Set<String> EXPECTED_KEYS = Set.of(
        "project.version",
        "buildNumber",
        "scmBranch",
        "build.timestamp"
    );

    public Map<String, Object> readVersionInfo() throws IOException {
        Properties props = new Properties();
        props.load(VersionInfoProvider.class.getResourceAsStream(VERSION_INFO_RESOURCE));
        return EXPECTED_KEYS.stream().collect(toMap(identity(), props::get));
    }
}
