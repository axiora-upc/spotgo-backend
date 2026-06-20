package com.axiora.spotgo.monitoring.domain.model.valueobjects;

public enum EmployeeRole {
    GUARD("guard"),
    CLEANING_PERSONNEL("cleaning-personnel");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EmployeeRole fromDisplayName(String displayName) {
        for (EmployeeRole r : values()) {
            if (r.displayName.equals(displayName)) return r;
        }
        return GUARD;
    }
}
