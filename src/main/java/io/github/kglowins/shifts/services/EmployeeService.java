package io.github.kglowins.shifts.services;

import static java.lang.String.format;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.controllers.exceptions.BadRequestException;
import io.github.kglowins.shifts.db.EmployeeDTO;
import io.github.kglowins.shifts.db.Repository;
import io.github.kglowins.shifts.db.ShiftDTO;
import java.util.List;


@Singleton
public class EmployeeService {
    private final Repository repository;

    @Inject
    public EmployeeService(Repository repository) {
        this.repository = repository;
    }

    public List<EmployeeDTO> getEmployees() {
        return repository.selectEmployees();
    }

    public EmployeeDTO getEmployees(Long id) {
        return repository.selectEmployee(id);
    }

    public void addEmployee(EmployeeDTO employeeDTO) {
        repository.insertEmployee(employeeDTO);
    }

    public void removeEmployee(Long id, Boolean removeShiftsBefore) {
        List<ShiftDTO> shiftsOfEmployee = repository.selectShiftsOfEmployee(id);
        List<Long> shiftsOfEmployeeIds = List.of();

        if (shiftsOfEmployee.size() > 0) {
            shiftsOfEmployeeIds = shiftsOfEmployee.stream().map(ShiftDTO::id).toList();
            if (!removeShiftsBefore) {
                throw new BadRequestException(format("""
                    Shifts of id=%s are assigned to employee of id=%d. Remove these shifts first. "
                    + "One can try DELETE /v1/employee/{id}?force=true""",
                    String.join(",", shiftsOfEmployeeIds.stream().map(Object::toString).toList()),
                    id
                    ));
            }
        }
        repository.deleteEmployee(id, shiftsOfEmployeeIds);
    }
}
