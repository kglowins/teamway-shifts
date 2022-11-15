package io.github.kglowins.shifts.controllers

import com.google.gson.Gson
import com.google.inject.Inject
import groovy.util.logging.Slf4j
import io.github.kglowins.shifts.controllers.exceptions.ErrorHandler
import io.github.kglowins.shifts.guicemodules.LocalDevModule
import io.github.kglowins.shifts.guicemodules.UtilModule
import io.github.kglowins.shifts.helpers.RequestSpecificationProvider
import io.github.kglowins.shifts.services.VersionInfoProvider
import io.github.kglowins.shifts.services.identity.IdentityService
import io.restassured.specification.RequestSpecification
import spock.guice.UseModules
import spock.lang.Shared
import spock.lang.Specification

import javax.inject.Named



@Slf4j
@UseModules([UtilModule, LocalDevModule])
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

    @Inject
    @Shared
    Gson gson

    @Shared
    RequestSpecification requestSpec = RequestSpecificationProvider.of(gson, baseUri)

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
        requestSpec.when().get(VERSION_ENDPOINT).then().log().all().statusCode(200)
    }

    def "should be unauthorized if not admin"() {
        given:
        identityService.isAdmin(_) >> false

        expect:
        requestSpec.when().get(VERSION_ENDPOINT).then().log().all().statusCode(403)
    }
}