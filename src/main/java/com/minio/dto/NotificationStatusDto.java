package com.minio.dto;

/**
 * DTO для статусов уведомлений
 */
public class NotificationStatusDto {
    
    private String status;
    private String description;
    
    public NotificationStatusDto() {}
    
    public NotificationStatusDto(String status, String description) {
        this.status = status;
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
