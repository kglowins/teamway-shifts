package io.github.kglowins.shifts.db;


import static io.github.kglowins.shifts.services.H2Runner.H2_URL;
import static io.github.kglowins.shifts.services.H2Runner.H2_USER;
import static io.jenetics.facilejdbc.Dctor.field;
import static io.jenetics.facilejdbc.Param.value;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.RowParser;
import io.jenetics.facilejdbc.UncheckedSQLException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;


@Singleton
@Slf4j
public class Repository {
    private static final String INSERT_EMPLOYEE = "INSERT INTO employees (last_name) VALUES (:last_name)";
    private static final String SELECT_EMPLOYEES = "SELECT id, last_name FROM employees";
    private static final String SELECT_EMPLOYEE = "SELECT id, last_name FROM employees WHERE id=:id";
    private static final String DELETE_EMPLOYEE = "DELETE FROM employees WHERE id=:id";

    private static final Dctor<EmployeeDTO> EMPLOYEE_DCTOR = Dctor.of(
        field("id", EmployeeDTO::id),
        field("last_name", EmployeeDTO::lastName)
    );
    private static final RowParser<EmployeeDTO> EMPLOYEE_PARSER = (row, conn) -> new EmployeeDTO(
        row.getLong("id"),
        row.getString("last_name")
    );

    private DataSource dataSource;

    @Inject
    public Repository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertEmployee(EmployeeDTO employeeDTO) {
        try {
            var connection = dataSource.getConnection();
            var query = Query.of(INSERT_EMPLOYEE).on(employeeDTO, EMPLOYEE_DCTOR);
            query.executeInsert(connection);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public void deleteEmployee(Long id) {
        try {
            var connection = dataSource.getConnection();
            var query = Query.of(DELETE_EMPLOYEE).on(value("id", id));
            query.executeUpdate(connection);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public List<EmployeeDTO> getEmployees()  {
        try {
            var connection = dataSource.getConnection();
            var query = Query.of(SELECT_EMPLOYEES);
            final List<EmployeeDTO> employees = query.as(EMPLOYEE_PARSER.list(), connection);
            return employees;
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public EmployeeDTO getEmployee(Long id)  {
        try {
            var connection = dataSource.getConnection();
            var query = Query.of(SELECT_EMPLOYEE).on(value("id", id));
            final EmployeeDTO employee = query
                .as(EMPLOYEE_PARSER.single(), connection);
            return employee;
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }

    }


}
