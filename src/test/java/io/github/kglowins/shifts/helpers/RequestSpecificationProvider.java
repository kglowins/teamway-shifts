package io.github.kglowins.shifts.helpers;


import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;

import com.google.gson.Gson;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;


public class RequestSpecificationProvider {

    private RequestSpecificationProvider() {
    }

    public static RequestSpecification of(Gson gson, String baseUri) {
        return given()
            .config(config()
                .objectMapperConfig(
                    new ObjectMapperConfig(ObjectMapperType.GSON)
                        .gsonObjectMapperFactory(
                            (type, str) -> gson
                        )
                )
            )
            .baseUri(baseUri)
            .log().all();
    }
}
