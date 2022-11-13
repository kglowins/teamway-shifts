package io.github.kglowins.shifts.services.identity;

import spark.Request;

public interface IdentityService {
    String getIdentity(Request request);
    boolean isAdmin(Request request);
}
