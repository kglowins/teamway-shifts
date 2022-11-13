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

@Singleton
public class EmployeeController {

    private static final String V1_EMPLOYEE = "/v1/employees";
    private static final String V1_EMPLOYEE_ID = V1_EMPLOYEE + "/:id";

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
        get(V1_EMPLOYEE, (request, response) -> {
            response.type(JSON_UTF_8.toString());
            return employeeService.getEmployees();
        }, gson::toJson);
    }

    private void createGetEmployeeEndpoint() {
        get(V1_EMPLOYEE_ID, (request, response) -> {
            response.type(JSON_UTF_8.toString());
            Long id = Long.parseLong(request.params("id"));
            return employeeService.getEmployees(id);
        }, gson::toJson);
    }

    private void createPostEmployeeEndpoint() {
        post(V1_EMPLOYEE, (request, response) -> {
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
            employeeService.removeEmployee(id, force);
            return "";
        }, gson::toJson);
    }
}
