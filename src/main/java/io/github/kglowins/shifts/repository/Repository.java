package io.github.kglowins.shifts.repository;


import static io.jenetics.facilejdbc.Dctor.field;
import static io.jenetics.facilejdbc.Param.value;
import static io.jenetics.facilejdbc.Param.values;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.controllers.dto.EmployeeDTO;
import io.github.kglowins.shifts.controllers.dto.ShiftDTO;
import io.github.kglowins.shifts.enums.ShiftWindow;
import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.RowParser;
import io.jenetics.facilejdbc.Transactional;
import io.jenetics.facilejdbc.UncheckedSQLException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Singleton
@Slf4j
public class Repository {

    private static final String INSERT_EMPLOYEE = "INSERT INTO employees (last_name) VALUES (:last_name)";
    private static final String SELECT_EMPLOYEES = "SELECT id, last_name FROM employees";
    private static final String SELECT_EMPLOYEE = "SELECT id, last_name FROM employees WHERE id=:id";
    private static final String DELETE_EMPLOYEE = "DELETE FROM employees WHERE id=:id";
    private static final String DELETE_SHIFTS = "DELETE FROM shifts WHERE id IN (:ids)";
    private static final String INSERT_SHIFT = """
        INSERT INTO shifts (employee_id, shift_date, shift_window)
        VALUES (:employee_id, :shift_date, :shift_window)
        """;
    private static final String SELECT_SHIFTS = "SELECT id, employee_id, shift_date, shift_window FROM shifts";
    private static final String EMPLOYEE_EXISTS_QUERY = "SELECT :id IN (SELECT id FROM employees)";
    private static final String SELECT_SHIFT_DAYS_OF_EMPLOYEE = "SELECT shift_date FROM shifts WHERE employee_id=:employee_id";
    private static final String SELECT_SHIFTS_OF_EMPLOYEE = """
        SELECT id, employee_id, shift_date, shift_window FROM shifts WHERE employee_id=:employee_id""";

    private static final String WIPE_OUT = """    
            DELETE FROM shifts;
            DELETE FROM employees;
            ALTER SEQUENCE employee_id_seq RESTART;
            ALTER SEQUENCE shift_id_seq RESTART;
        """;

    private static final Dctor<EmployeeDTO> EMPLOYEE_DCTOR = Dctor.of(
        field("id", EmployeeDTO::id),
        field("last_name", dto -> dto.lastName().trim())
    );

    private static final RowParser<EmployeeDTO> EMPLOYEE_PARSER = (row, conn) -> new EmployeeDTO(
        row.getLong("id"),
        row.getString("last_name")
    );

    private static final RowParser<ShiftDTO> SHIFT_PARSER = (row, conn) -> new ShiftDTO(
        row.getLong("id"),
        row.getLong("employee_id"),
        row.getDate("shift_date").toLocalDate(),
        ShiftWindow.fromDisplayName(row.getString("shift_window"))
    );

    private static final Dctor<ShiftDTO> SHIFT_DCTOR = Dctor.of(
        field("id", ShiftDTO::id),
        field("employee_id", ShiftDTO::employeeId),
        field("shift_date", dto -> Date.valueOf(dto.shiftDate())),
        field("shift_window", dto -> dto.shiftWindow().displayName())
    );

    private final DataSource dataSource;

    @Inject
    public Repository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SneakyThrows
    public void wipeOutTables() {
        log.info("Removing rows from tables and restarting sequences");
        var connection = dataSource.getConnection();
        var query = Query.of(WIPE_OUT);
        query.executeInsert(connection);
    }

    @SneakyThrows
    public void insertEmployee(EmployeeDTO employeeDTO) {
        var connection = dataSource.getConnection();
        var query = Query.of(INSERT_EMPLOYEE).on(employeeDTO, EMPLOYEE_DCTOR);
        query.executeInsert(connection);
    }

    @SneakyThrows
    public List<EmployeeDTO> selectEmployees()  {
        var connection = dataSource.getConnection();
        var query = Query.of(SELECT_EMPLOYEES);
        return query.as(EMPLOYEE_PARSER.list(), connection);
    }

    @SneakyThrows
    public EmployeeDTO selectEmployee(Long id)  {
        var connection = dataSource.getConnection();
        var query = Query.of(SELECT_EMPLOYEE).on(value("id", id));
        return query.as(EMPLOYEE_PARSER.single(), connection);
    }

    @SneakyThrows
    public List<ShiftDTO> selectShifts() {
        var connection = dataSource.getConnection();
        var query = Query.of(SELECT_SHIFTS);
        return query.as(SHIFT_PARSER.list(), connection);
    }

    @SneakyThrows
    public void insertShift(ShiftDTO shiftDTO) {
        var connection = dataSource.getConnection();
        var query = Query.of(INSERT_SHIFT).on(shiftDTO, SHIFT_DCTOR);
        query.executeInsert(connection);
    }

    @SneakyThrows
    public void deleteShifts(List<Long> shiftIds) {
        var connection = dataSource.getConnection();
        var query = Query.of(DELETE_SHIFTS).on(values("ids", shiftIds));
        query.executeInsert(connection);
    }

    @SneakyThrows
    public Boolean employeeExists(Long candidateEmployeeId) {
        var connection = dataSource.getConnection();
        var query = Query.of(EMPLOYEE_EXISTS_QUERY).on(values("id", candidateEmployeeId));
        return query.as(RowParser.bool(1).single(), connection);
    }

    @SneakyThrows
    public List<LocalDate> selectShiftDaysOfEmployee(Long employeeId) {
        var connection = dataSource.getConnection();
        var query = Query.of(SELECT_SHIFT_DAYS_OF_EMPLOYEE).on(values("employee_id", employeeId));
        List<Instant> shiftDaysAsInstant = query.as(RowParser.instant(1).list(), connection);
        return shiftDaysAsInstant.stream().map(i -> LocalDate.ofInstant(i, ZoneId.systemDefault())).toList();
    }

    @SneakyThrows
    public List<ShiftDTO> selectShiftsOfEmployee(Long employeeId) {
        var connection = dataSource.getConnection();
        var query = Query.of(SELECT_SHIFTS_OF_EMPLOYEE).on(values("employee_id", employeeId));
        return query.as(SHIFT_PARSER.list(), connection);
    }

    @SneakyThrows
    public void deleteEmployee(Long employeeId, List<Long> shiftIds) {
        final Transactional transactional = dataSource::getConnection;
        transactional.transaction().accept(connection -> {
            try {
                if (!shiftIds.isEmpty()) {
                    var deleteShiftsQuery = Query.of(DELETE_SHIFTS).on(values("ids", shiftIds));
                    deleteShiftsQuery.executeInsert(connection);
                }
                var deleteEmployeeQuery = Query.of(DELETE_EMPLOYEE).on(value("id", employeeId));
                deleteEmployeeQuery.executeUpdate(connection);
            } catch (SQLException e) {
                throw new UncheckedSQLException(e);
            }
        });
    }
}
