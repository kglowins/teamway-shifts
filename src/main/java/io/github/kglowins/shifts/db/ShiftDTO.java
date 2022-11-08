package io.github.kglowins.shifts.db;

import java.util.Date;

public record ShiftDTO (Long id, Long employeeId, Date shiftDate, ShiftWindow shiftWindow) {
}
