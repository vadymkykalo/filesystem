package com.minio.model;

public enum LogicalOperator {
    AND("И"),
    OR("ИЛИ");
    
    private final String displayName;
    
    LogicalOperator(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
