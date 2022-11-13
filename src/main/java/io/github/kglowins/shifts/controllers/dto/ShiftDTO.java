package io.github.kglowins.shifts.controllers.dto;

import io.github.kglowins.shifts.enums.ShiftWindow;
import java.time.LocalDate;

public record ShiftDTO(Long id, Long employeeId, LocalDate shiftDate, ShiftWindow shiftWindow) {
}
