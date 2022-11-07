package io.github.kglowins.shifts.services;

import static java.lang.String.format;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.kglowins.shifts.controllers.exceptions.BadRequestException;
import io.github.kglowins.shifts.controllers.exceptions.NotFoundException;
import io.github.kglowins.shifts.db.Repository;
import io.github.kglowins.shifts.db.ShiftDTO;
import java.util.Date;
import java.util.List;

@Singleton
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

        List<Date> shiftDaysOfEmployee = repository.selectShiftDaysOfEmployee(shiftDTO.employeeId());
        if (shiftDaysOfEmployee.contains(shiftDTO.shiftDate())) {
            throw new BadRequestException(format("Employee of id=%d already has shift on %s",
                    shiftDTO.employeeId(),
                    shiftDTO.shiftDate().toString()));
        }

        repository.insertShift(shiftDTO);
    }

    public void removeShifts(List<Long> shiftIds) {
        repository.deleteShifts(shiftIds);
    }

    public List<ShiftDTO> getShiftsOfEmployee(Long employeeId) {
        return repository.selectShiftsOfEmployee(employeeId);
    }
}
