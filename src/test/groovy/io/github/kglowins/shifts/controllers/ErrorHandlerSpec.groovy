package io.github.kglowins.shifts.controllers

import com.google.gson.Gson
import com.google.inject.Inject
import io.github.kglowins.shifts.controllers.dto.ErrorMessageDTO
import io.github.kglowins.shifts.controllers.exceptions.BadRequestException
import io.github.kglowins.shifts.controllers.exceptions.ErrorHandler
import io.github.kglowins.shifts.guicemodules.LocalDevModule
import io.github.kglowins.shifts.services.GsonProvider
import io.restassured.config.ObjectMapperConfig
import io.restassured.mapper.ObjectMapperType
import spock.guice.UseModules
import spock.lang.Specification

import javax.inject.Named

import static io.github.kglowins.shifts.enums.Environment.PROD
import spock.lang.Shared

import static io.restassured.RestAssured.config
import static io.restassured.RestAssured.given
import static spark.Spark.get

@UseModules(LocalDevModule)
class ErrorHandlerSpec extends Specification {

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
    def TEST_ENDPOINT = "/test"

    def "should not display stacktrace for non-dev env"() {
        given:
        def errorHandler = new ErrorHandler(PROD)
        get(TEST_ENDPOINT, (request, response) -> {
            throw new BadRequestException("bad request")
        }, new Gson()::toJson);

        when:
        def response = givenBaseUri()
                .when()
                .get(TEST_ENDPOINT)
                .then()
                .log().all()
                .extract().as(ErrorMessageDTO.class)

        then:
        response.stackTrace() == null
    }
}
