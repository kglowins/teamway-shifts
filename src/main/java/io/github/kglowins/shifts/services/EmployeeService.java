package io.github.kglowins.shifts.services;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.controllers.exceptions.BadRequestException;
import io.github.kglowins.shifts.controllers.dto.EmployeeDTO;
import io.github.kglowins.shifts.controllers.exceptions.NotFoundException;
import io.github.kglowins.shifts.repository.Repository;
import io.github.kglowins.shifts.controllers.dto.ShiftDTO;
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
        if (!repository.employeeExists(id)) {
            throw new NotFoundException(format("Employee of id=%d does not exist", id));
        }
        return repository.selectEmployee(id);
    }

    public void addEmployee(EmployeeDTO employeeDTO) {
        if (isNull(employeeDTO.lastName()) || (nonNull(employeeDTO.lastName()) && employeeDTO.lastName().trim().isEmpty())) {
            throw new BadRequestException("Employee last name cannot be null or empty");
        }
        repository.insertEmployee(employeeDTO);
    }

    public void removeEmployee(Long id, Boolean removeShiftsBefore) {
        if (!repository.employeeExists(id)) {
            throw new NotFoundException(format("Employee of id=%d does not exist", id));
        }
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
