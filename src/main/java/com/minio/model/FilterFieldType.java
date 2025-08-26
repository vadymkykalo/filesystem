package com.minio.model;

public enum FilterFieldType {
    SMID("SMID"),
    DISTRIBUTION_GROUPS_FILE("DISTRIBUTION_GROUPS_FILE"),
    COUNTRY("COUNTRY"),
    OPERATING_SYSTEM("OPERATING_SYSTEM"),
    CLIENT_VERSION("CLIENT_VERSION"),
    BROWSER("BROWSER"),
    INTERNET_TYPE("INTERNET_TYPE"),
    INTERNAL_TRANSITION("INTERNAL_TRANSITION"),
    CLE_CAMPAIGN("CLE_CAMPAIGN");
    
    private final String displayName;
    
    FilterFieldType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
