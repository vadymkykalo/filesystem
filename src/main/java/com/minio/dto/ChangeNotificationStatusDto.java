package com.minio.dto;

/**
 * DTO для изменения статуса уведомления
 */
public class ChangeNotificationStatusDto {
    
    private String status;
    
    public ChangeNotificationStatusDto() {}
    
    public ChangeNotificationStatusDto(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
