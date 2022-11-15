package io.github.kglowins.shifts.controllers

import com.google.gson.Gson
import com.google.inject.Inject
import io.github.kglowins.shifts.controllers.dto.ErrorMessageDTO
import io.github.kglowins.shifts.controllers.exceptions.BadRequestException
import io.github.kglowins.shifts.controllers.exceptions.ErrorHandler
import io.github.kglowins.shifts.guicemodules.LocalDevModule
import io.github.kglowins.shifts.guicemodules.UtilModule
import io.github.kglowins.shifts.helpers.RequestSpecificationProvider
import io.restassured.specification.RequestSpecification
import spock.guice.UseModules
import spock.lang.Specification
import javax.inject.Named
import static io.github.kglowins.shifts.enums.Environment.PROD
import spock.lang.Shared
import static spark.Spark.get


@UseModules([UtilModule, LocalDevModule])
class ErrorHandlerSpec extends Specification {

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
    def TEST_ENDPOINT = "/test"

    def "should not display stacktrace for non-dev env"() {
        given:
        def errorHandler = new ErrorHandler(PROD)
        get(TEST_ENDPOINT, (request, response) -> {
            throw new BadRequestException("bad request")
        }, gson::toJson);

        when:
        def response = requestSpec
                .when()
                .get(TEST_ENDPOINT)
                .then()
                .log().all()
                .extract().as(ErrorMessageDTO.class)

        then:
        response.stackTrace() == null
    }
}
