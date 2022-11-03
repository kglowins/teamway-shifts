package io.github.kglowins.shifts.controllers;


import static com.google.common.net.MediaType.JSON_UTF_8;
import static spark.Spark.get;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.services.VersionInfoProvider;

@Singleton
public class VersionInfoController {
    private final VersionInfoProvider versionInfoProvider;

    @Inject
    public VersionInfoController(VersionInfoProvider basicInfoProvider) {
        this.versionInfoProvider = basicInfoProvider;
        createVersionInfoEndpoint();
    }

    private void createVersionInfoEndpoint() {
        get("/version", (request, response) -> {
            response.type(JSON_UTF_8.toString());
            return versionInfoProvider.readVersionInfo();
        }, new Gson()::toJson);
    }
}
