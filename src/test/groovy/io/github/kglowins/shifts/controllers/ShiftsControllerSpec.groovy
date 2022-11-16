package io.github.kglowins.shifts.controllers

import com.google.inject.Inject
import groovy.util.logging.Slf4j
import io.github.kglowins.shifts.controllers.dto.EmployeeDTO
import io.github.kglowins.shifts.controllers.dto.ShiftDTO
import io.github.kglowins.shifts.services.EmployeeService
import spock.lang.Unroll

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static io.github.kglowins.shifts.enums.ShiftWindow.FROM_00_TO_08
import static io.github.kglowins.shifts.enums.ShiftWindow.FROM_08_TO_16
import static io.github.kglowins.shifts.enums.ShiftWindow.FROM_16_TO_24
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Stepwise


import static io.restassured.http.ContentType.JSON

@Stepwise
@Slf4j
class ShiftsControllerSpec extends BaseControllerSpec {

    @Shared
    def SHIFTS_ENDPOINT = "/v1/shifts"

    @Shared
    @Inject
    EmployeeService employeeService

    @Shared
    @Inject
    ShiftsController shiftsController

    @Shared
    @Inject
    EmployeeController employeeController


    def setupSpec() {
        repository.wipeOutTables()
        employeeService.addEmployee(new EmployeeDTO(null, "Glowinski"))
        employeeService.addEmployee(new EmployeeDTO(null, "Teamway"))
    }

    @Unroll
    def "should create shift #employeeId #shiftDate #shiftWindow"(employeeId, shiftDate, shiftWindow) {
        given:
        def shiftDTO = new ShiftDTO(null,
                employeeId,
                LocalDate.parse(shiftDate, DateTimeFormatter.ISO_LOCAL_DATE),
                shiftWindow)

        expect:
        requestSpec
                .contentType(JSON)
                .body(shiftDTO)
                .when()
                .post(SHIFTS_ENDPOINT)
                .then()
                .log().all()
                .statusCode(201)

        where:
        employeeId | shiftDate    | shiftWindow
        1L         | "2022-11-11" | FROM_08_TO_16
        1L         | "2022-11-12" | FROM_00_TO_08
        2L         | "2022-11-11" | FROM_16_TO_24
    }

    def "should not create shift for non-existing employee"() {
        given:
        def shiftDTO = new ShiftDTO(null, 9L, LocalDate.parse("2022-11-12", DateTimeFormatter.ISO_LOCAL_DATE), FROM_08_TO_16)

        expect:
        requestSpec
                .contentType(JSON)
                .body(shiftDTO)
                .when()
                .post(SHIFTS_ENDPOINT)
                .then()
                .log().all()
                .statusCode(404)
    }

    def "should not add second shift for the same employee on the same day"() {
        given:
        def shiftDTO = new ShiftDTO(null, 1L, LocalDate.parse("2022-11-12", DateTimeFormatter.ISO_LOCAL_DATE), FROM_08_TO_16)

        expect:
        requestSpec
                .contentType(JSON)
                .body(shiftDTO)
                .when()
                .post(SHIFTS_ENDPOINT)
                .then()
                .log().all()
                .statusCode(400)
    }

    @Ignore("Not implemented")
    def "should get shift"() {

    }

    @Ignore("Not implemented")
    def "should return 404 for get non-existing shift"() {
    }

    def "should get shifts for a given employee"() {
        given:
        def response = requestSpec
            .when()
            .get(SHIFTS_ENDPOINT + "/employee/1")
            .then()
            .log().all()

        expect:
        response.statusCode(200)
        response.extract().as(ShiftDTO[].class).length == 2
    }

    @Ignore("Not implemented")
    def "should get shifts list"() {
    }

    @Ignore("Not implemented")
    def "should not create shift with invalid shift_window"() {
    }

    @Ignore("Not implemented")
    def "should delete shift"() {
    }

    // TODO move below 2 to a separate Spec with e2e tests
    def "should not delete employee with existing shifts"() {
        given:
        def delEmployeeResponse = requestSpec
                .when()
                .delete("/v1/employees/1")
                .then()
                .log().all()

        expect:
        delEmployeeResponse.statusCode(400)
    }

    def "should delete shifts when employee is deleted with force query param"() {
        given:
        def delEmployeeResponse = requestSpec
                .when()
                .delete("/v1/employees/1?force")
                .then()
                .log().all()
        when:
        def getShiftsResponse = requestSpec
                .when()
                .get(SHIFTS_ENDPOINT + "/employee/1")
                .then()
                .log().all()

        then:
        delEmployeeResponse.statusCode(204)
        getShiftsResponse.statusCode(200)
        getShiftsResponse.extract().as(ShiftDTO[].class).length == 0
    }

}
