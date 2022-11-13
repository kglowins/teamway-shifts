package io.github.kglowins.shifts.controllers

import com.google.inject.Inject
import io.github.kglowins.shifts.controllers.dto.EmployeeDTO
import spock.lang.Unroll

import static io.restassured.http.ContentType.JSON
import spock.lang.Shared
import spock.lang.Stepwise


@Stepwise
class EmployeesControllerSpec extends BaseControllerSpec {

    @Shared
    def EMPLOYEES_ENDPOINT = "/v1/employees"

    @Shared
    def LAST_NAME_2 = "Teamway"

    @Shared
    @Inject
    EmployeeController employeeController

    @Unroll
    def "should add employee(s)"(lastName) {
        given:
        def employeeDTO = new EmployeeDTO(null, lastName)

        expect:
        givenBaseUri()
                .contentType(JSON)
                .body(employeeDTO)
                .when()
                .post(EMPLOYEES_ENDPOINT)
                .then()
                .log().all()
                .statusCode(201)

        where:
        lastName    | _
        "Glowinski" | _
        LAST_NAME_2 | _
    }

    @Unroll
    def "should not add employee when required field missing or empty"(lastName) {
        given:
        def employeeDTO = new EmployeeDTO(null, lastName)

        expect:
        givenBaseUri()
                .contentType(JSON)
                .body(employeeDTO)
                .when()
                .post(EMPLOYEES_ENDPOINT)
                .then()
                .log().all()
                .statusCode(400)

        where:
        lastName | _
        ""       | _
        null     | _
    }

    def "should not add employee when invalid field name"() {
        given:
        givenBaseUri()
                .contentType(JSON)
                .body("{\"last name\":\"No underscore\"}")
                .when()
                .post(EMPLOYEES_ENDPOINT)
                .then()
                .log().all()
                .statusCode(400)
    }

    def "should get employee with incremented id" () {
        given:
        def response = givenBaseUri()
                .when()
                .get(EMPLOYEES_ENDPOINT + "/2")
                .then()
                .log().all()

        expect:
        response.statusCode(200)
        response.extract().body().as(EmployeeDTO.class).lastName() == LAST_NAME_2
    }

    def "should get employees list" () {
        given:
        def response = givenBaseUri()
                .when()
                .get(EMPLOYEES_ENDPOINT)
                .then()
                .log().all()

        expect:
        response.statusCode(200)
        response.extract().body().as(EmployeeDTO[].class).length == 2
    }

    def "should delete employee"() {
        given:
        def response = givenBaseUri()
                .when()
                .delete(EMPLOYEES_ENDPOINT + "/1")
                .then()
                .log().all()

        expect:
        response.statusCode(204)
        employeeController.employeeService.getEmployees().size() == 1
    }

    def "should warn when try to get non-existing employee"() {
        given:
        def response = givenBaseUri()
                .when()
                .get(EMPLOYEES_ENDPOINT + "/9")
                .then()
                .log().all()

        expect:
        response.statusCode(404)
    }

    def "should warn when try to delete non-existing employee"() {
        given:
        def response = givenBaseUri()
                .when()
                .delete(EMPLOYEES_ENDPOINT + "/9")
                .then()
                .log().all()

        expect:
        response.statusCode(404)
    }
}
