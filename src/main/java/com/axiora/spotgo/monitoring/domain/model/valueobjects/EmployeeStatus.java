package com.axiora.spotgo.monitoring.domain.model.valueobjects;

public enum EmployeeStatus {
    ON_DUTY("on-duty"),
    OFF_DUTY("off-duty");

    private final String displayName;

    EmployeeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EmployeeStatus fromDisplayName(String displayName) {
        for (EmployeeStatus s : values()) {
            if (s.displayName.equals(displayName)) return s;
        }
        return ON_DUTY;
    }
}
