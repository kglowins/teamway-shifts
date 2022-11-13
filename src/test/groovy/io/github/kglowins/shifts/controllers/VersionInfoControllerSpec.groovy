package io.github.kglowins.shifts.controllers

import com.google.inject.Inject
import groovy.util.logging.Slf4j
import io.github.kglowins.shifts.controllers.exceptions.ErrorHandler
import io.github.kglowins.shifts.guicemodules.LocalDevModule
import io.github.kglowins.shifts.services.GsonProvider
import io.github.kglowins.shifts.services.VersionInfoProvider
import io.github.kglowins.shifts.services.identity.IdentityService
import io.restassured.config.ObjectMapperConfig
import io.restassured.mapper.ObjectMapperType
import spock.guice.UseModules
import spock.lang.Shared
import spock.lang.Specification

import javax.inject.Named

import static io.restassured.RestAssured.config
import static io.restassured.RestAssured.given


@Slf4j
@UseModules(LocalDevModule)
class VersionInfoControllerSpec extends Specification {

    @Shared
    def mockedVersionInfo = ["scmBranch": "main",
                             "build.timestamp": "2022-11-08 23:22:31",
                             "project.version": "1.0.0-SNAPSHOT",
                             "buildNumber": "981e8b"]
    @Shared
    def VERSION_ENDPOINT = "/monitoring/version"

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

    IdentityService identityService
    VersionInfoProvider versionInfoProvider

    def setup() {
        identityService = Mock()
        versionInfoProvider = Mock()
        versionInfoProvider.readVersionInfo() >> mockedVersionInfo
        def versionInfoController = new VersionInfoController(versionInfoProvider, identityService)
    }

    def "should display version info when admin"() {
        given:
        identityService.isAdmin(_) >> true

        expect:
        givenBaseUri().when().get(VERSION_ENDPOINT).then().log().all().statusCode(200)
    }

    def "should be unauthorized if not admin"() {
        given:
        identityService.isAdmin(_) >> false

        expect:
        givenBaseUri().when().get(VERSION_ENDPOINT).then().log().all().statusCode(403)
    }
}