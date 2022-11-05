package io.github.kglowins.shifts.controllers;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.db.EmployeeDTO;
import io.github.kglowins.shifts.services.EmployeeService;

@Singleton
public class EmployeeController {

    private final EmployeeService employeeService;

    private final Gson gson;

    @Inject
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
        gson = new Gson();
        createGetEmployeesEndpoint();
        createGetEmployeeEndpoint();
        createPostEmployeeEndpoint();
        createDeleteEmployeeEndpoint();
    }

    private void createGetEmployeesEndpoint() {
        get("/v1/employee", (request, response) -> {
            response.type(JSON_UTF_8.toString());
            return employeeService.getEmployees();
        }, new Gson()::toJson);
    }

    private void createGetEmployeeEndpoint() {
        get("/v1/employee/:id", (request, response) -> {
            response.type(JSON_UTF_8.toString());
            Long id = Long.parseLong(request.params("id"));
            return employeeService.getEmployees(id);
        }, new Gson()::toJson);
    }

    private void createPostEmployeeEndpoint() {
        post("/v1/employee", (request, response) -> {
            EmployeeDTO employeeDTO = gson.fromJson(request.body(), EmployeeDTO.class);
            employeeService.addEmployee(employeeDTO);
            response.status(SC_CREATED);
            return "";
        });
    }

    private void createDeleteEmployeeEndpoint() {
        delete("/v1/employee/:id", (request, response) -> {
            response.status(SC_NO_CONTENT);
            Long id = Long.parseLong(request.params("id"));
            employeeService.removeEmployee(id);
            return "";
        }, new Gson()::toJson);
    }
}
