package io.github.kglowins.shifts.enums;

public enum ShiftWindow {
    FROM_00_TO_08("00-08"),
    FROM_08_TO_16("08-16"),
    FROM_16_TO_24("16-24");

    private String displayName;

    ShiftWindow(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static ShiftWindow fromDisplayName(String displayName) {
        for (ShiftWindow sw : ShiftWindow.values()) {
            if (sw.displayName().equals(displayName)) {
                return sw;
            }
        }
        throw new IllegalArgumentException("Given display name don't match any ShiftWindow enum constant");
    }
}
