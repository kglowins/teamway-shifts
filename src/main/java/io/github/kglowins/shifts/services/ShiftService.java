package io.github.kglowins.shifts.services;

import static java.lang.String.format;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.controllers.exceptions.BadRequestException;
import io.github.kglowins.shifts.controllers.exceptions.NotFoundException;
import io.github.kglowins.shifts.repository.Repository;
import io.github.kglowins.shifts.controllers.dto.ShiftDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ShiftService {

    private final Repository repository;

    @Inject
    public ShiftService(Repository repository) {
        this.repository = repository;
    }

    public List<ShiftDTO> getShifts() {
        return repository.selectShifts();
    }

    public void addShift(ShiftDTO shiftDTO) {
        if (!repository.employeeExists(shiftDTO.employeeId())) {
            throw new NotFoundException(format(
                "Employee of id=%d does not exist. Cannot add shift for non-existing employee",
                shiftDTO.employeeId()));
        }

        List<LocalDate> shiftDaysOfEmployee = repository.selectShiftDaysOfEmployee(shiftDTO.employeeId());
        shiftDaysOfEmployee.forEach(d -> {
               if (d.equals(shiftDTO.shiftDate())) {
                throw new BadRequestException(format("Employee of id=%d already has shift on %s",
                    shiftDTO.employeeId(),
                    shiftDTO.shiftDate()));
            }
        });
        repository.insertShift(shiftDTO);
    }

    public void removeShifts(List<Long> shiftIds) {
        repository.deleteShifts(shiftIds);
    }

    public List<ShiftDTO> getShiftsOfEmployee(Long employeeId) {
        return repository.selectShiftsOfEmployee(employeeId);
    }
}
