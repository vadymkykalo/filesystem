package com.minio.dto;

import jakarta.ws.rs.core.MediaType;

/**
 * DTO для маркетинговых целей
 */
public class MarketingTargetDto {
    
    private Long id;
    private String name;
    private String targetType;
    private String status;
    
    public MarketingTargetDto() {}
    
    public MarketingTargetDto(Long id, String name, String targetType, String status) {
        this.id = id;
        this.name = name;
        this.targetType = targetType;
        this.status = status;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
