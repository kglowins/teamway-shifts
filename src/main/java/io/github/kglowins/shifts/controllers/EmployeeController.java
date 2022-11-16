package io.github.kglowins.shifts.controllers;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static java.util.stream.Collectors.toSet;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.controllers.dto.EmployeeDTO;
import io.github.kglowins.shifts.services.EmployeeService;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class EmployeeController {

    private static final String V1_EMPLOYEES = "/v1/employees";
    private static final String V1_EMPLOYEE_ID = V1_EMPLOYEES + "/:id";

    private final EmployeeService employeeService;

    private final Gson gson;

    @Inject
    public EmployeeController(EmployeeService employeeService, Gson gson) {
        this.employeeService = employeeService;
        this.gson = gson;
        createGetEmployeesEndpoint();
        createGetEmployeeEndpoint();
        createPostEmployeeEndpoint();
        createDeleteEmployeeEndpoint();
    }

    private void createGetEmployeesEndpoint() {
        get(V1_EMPLOYEES, (request, response) -> {
            log.info("Received GET {}", V1_EMPLOYEES);
            response.type(JSON_UTF_8.toString());
            return employeeService.getEmployees();
        }, gson::toJson);
    }

    private void createGetEmployeeEndpoint() {
        get(V1_EMPLOYEE_ID, (request, response) -> {
            response.type(JSON_UTF_8.toString());
            Long id = Long.parseLong(request.params("id"));
            log.info("Received GET {}/{}", V1_EMPLOYEES, id);
            return employeeService.getEmployees(id);
        }, gson::toJson);
    }

    private void createPostEmployeeEndpoint() {
        post(V1_EMPLOYEES, (request, response) -> {
            log.info("Received POST {} with body: {}", V1_EMPLOYEES , request.body());
            EmployeeDTO employeeDTO = gson.fromJson(request.body(), EmployeeDTO.class);
            employeeService.addEmployee(employeeDTO);
            response.status(SC_CREATED);
            return "";
        });
    }

    private void createDeleteEmployeeEndpoint() {
        delete(V1_EMPLOYEE_ID, (request, response) -> {
            response.status(SC_NO_CONTENT);
            Long id = Long.parseLong(request.params("id"));
            Boolean force = request.queryParams().stream().map(String::toLowerCase).collect(toSet()).contains("force");
            log.info("Received GET {}/{}, force={}", V1_EMPLOYEES, id, force);
            employeeService.removeEmployee(id, force);
            return "";
        }, gson::toJson);
    }
}
