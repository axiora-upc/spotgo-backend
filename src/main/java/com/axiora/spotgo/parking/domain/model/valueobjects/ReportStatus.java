package com.axiora.spotgo.parking.domain.model.valueobjects;

public enum ReportStatus {
    SUBMITTED("submitted"),
    RESOLVED("resolved");

    private final String displayName;

    ReportStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReportStatus fromDisplayName(String displayName) {
        for (ReportStatus s : values()) {
            if (s.displayName.equals(displayName)) return s;
        }
        return SUBMITTED;
    }
}
