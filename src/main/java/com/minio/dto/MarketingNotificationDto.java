package com.minio.dto;

/**
 * DTO для маркетинговых уведомлений
 */
public class MarketingNotificationDto {
    
    private Long id;
    private String title;
    private String message;
    private String status;
    private Long marketingTargetId;
    
    public MarketingNotificationDto() {}
    
    public MarketingNotificationDto(Long id, String title, String message, String status, Long marketingTargetId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.status = status;
        this.marketingTargetId = marketingTargetId;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getMarketingTargetId() {
        return marketingTargetId;
    }
    
    public void setMarketingTargetId(Long marketingTargetId) {
        this.marketingTargetId = marketingTargetId;
    }
}
