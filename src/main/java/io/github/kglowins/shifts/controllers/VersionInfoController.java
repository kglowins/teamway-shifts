package io.github.kglowins.shifts.controllers;


import static com.google.common.net.MediaType.JSON_UTF_8;
import static spark.Spark.get;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.controllers.exceptions.UnauthorizedException;
import io.github.kglowins.shifts.services.VersionInfoProvider;
import io.github.kglowins.shifts.services.identity.IdentityService;
import spark.Request;

@Singleton
public class VersionInfoController {
    private final VersionInfoProvider versionInfoProvider;
    private final IdentityService identityService;

    @Inject
    public VersionInfoController(VersionInfoProvider versionInfoProvider, IdentityService identityService) {
        this.versionInfoProvider = versionInfoProvider;
        this.identityService = identityService;
        createVersionInfoEndpoint();
    }

    private void createVersionInfoEndpoint() {
        get("/monitoring/version", (request, response) -> {
            checkIfAdmin(request);
            response.type(JSON_UTF_8.toString());
            return versionInfoProvider.readVersionInfo();
        }, new Gson()::toJson);
    }

    private void checkIfAdmin(Request request) {
        if (!identityService.isAdmin(request)) {
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
