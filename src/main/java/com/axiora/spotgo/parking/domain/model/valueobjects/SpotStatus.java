package com.axiora.spotgo.parking.domain.model.valueobjects;

public enum SpotStatus {
    FREE("available"),
    OCCUPIED("occupied"),
    RESERVED("reserved"),
    MAINTENANCE("maintenance");

    private final String displayName;

    SpotStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SpotStatus fromDisplayName(String displayName) {
        for (SpotStatus s : values()) {
            if (s.displayName.equals(displayName)) return s;
        }
        return FREE;
    }
}
