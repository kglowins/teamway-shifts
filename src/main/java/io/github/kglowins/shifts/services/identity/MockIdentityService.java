package io.github.kglowins.shifts.services.identity;

import spark.Request;

public class MockIdentityService implements IdentityService {

    @Override
    public String getIdentity(Request request) {
        return "kglowins";
    }

    @Override
    public boolean isAdmin(Request request) {
        return true;
    }
}
