package io.github.kglowins.shifts.controllers

import com.google.inject.Inject
import io.github.kglowins.shifts.controllers.exceptions.ErrorHandler
import io.github.kglowins.shifts.guicemodules.CommonModule
import io.github.kglowins.shifts.guicemodules.LocalDevModule
import io.github.kglowins.shifts.services.GsonProvider
import io.restassured.config.ObjectMapperConfig
import io.restassured.mapper.ObjectMapperType
import spock.guice.UseModules

import javax.inject.Named
import spock.lang.Shared
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static io.restassured.RestAssured.config

@UseModules([CommonModule, LocalDevModule])
class BaseControllerSpec extends Specification {

    @Shared
    @Inject
    @Named("localhost")
    String baseUri

    def givenBaseUri() {
        return given()
                .config(config()
                        .objectMapperConfig(
                                new ObjectMapperConfig(ObjectMapperType.GSON)
                                .gsonObjectMapperFactory(
                                        (type, str) -> GsonProvider.provide()
                                )
                        )
                )
                .baseUri(baseUri)
                .log().all()
    }

    @Shared
    @Inject
    ErrorHandler errorHandler
}
