package com.minio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MARKETING_TARGET")
public class MarketingTarget extends ABase {
    
    public enum MarketingTargetStatus {
        CREATED,    // Just created
        LOADING,    // SMID loading usage
        READY,      // All SMIDs loaded
        FAILED      // Error loading
    }
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TARGET_TYPE", length = 20, nullable = false)
    private TargetType targetType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private MarketingTargetStatus status;
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public TargetType getTargetType() {
        return targetType;
    }
    
    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }
    
    public MarketingTargetStatus getStatus() {
        return status;
    }
    
    public void setStatus(MarketingTargetStatus status) {
        this.status = status;
    }
}
