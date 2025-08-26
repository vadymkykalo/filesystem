package com.minio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MARKETING_PUSH_MESSAGE")
@NamedEntityGraph(name = "MarketingPushMessage.marketingTarget", 
                  attributeNodes = @NamedAttributeNode("marketingTarget"))
public class MarketingPushMessage extends ABase {
    
    public enum Status {
        INACTIVE, SENT
    }
    
    @Column(name = "OBJECT_ID")
    private Long objectId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private Status status;
    
    @Column(name = "MARKETING_TARGET_ID")
    private Long marketingTargetId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARKETING_TARGET_ID", insertable = false, updatable = false)
    private MarketingTarget marketingTarget;
    
    @Column(name = "LAST_USER_CHANGE", nullable = false)
    private Long lastUserChange;
    
    @Column(name = "NAME_COMPANY", length = 100, nullable = false)
    private String nameCompany;
    
    @Column(name = "TITLE_UA", length = 100, nullable = false)
    private String titleUa;
    
    @Column(name = "TEXT_UA", length = 300, nullable = false)
    private String textUa;
    
    @Column(name = "URL", length = 255)
    private String url;
    
    @Column(name = "MARKETING_DEEPLINK_ID")
    private Long marketingDeeplinkId;
    
    // Getters and setters
    
    public Long getObjectId() {
        return objectId;
    }
    
    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Long getMarketingTargetId() {
        return marketingTargetId;
    }
    
    public void setMarketingTargetId(Long marketingTargetId) {
        this.marketingTargetId = marketingTargetId;
    }
    
    public MarketingTarget getMarketingTarget() {
        return marketingTarget;
    }
    
    public void setMarketingTarget(MarketingTarget marketingTarget) {
        this.marketingTarget = marketingTarget;
    }
    
    public Long getLastUserChange() {
        return lastUserChange;
    }
    
    public void setLastUserChange(Long lastUserChange) {
        this.lastUserChange = lastUserChange;
    }
    
    public String getNameCompany() {
        return nameCompany;
    }
    
    public void setNameCompany(String nameCompany) {
        this.nameCompany = nameCompany;
    }
    
    public String getTitleUa() {
        return titleUa;
    }
    
    public void setTitleUa(String titleUa) {
        this.titleUa = titleUa;
    }
    
    public String getTextUa() {
        return textUa;
    }
    
    public void setTextUa(String textUa) {
        this.textUa = textUa;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Long getMarketingDeeplinkId() {
        return marketingDeeplinkId;
    }
    
    public void setMarketingDeeplinkId(Long marketingDeeplinkId) {
        this.marketingDeeplinkId = marketingDeeplinkId;
    }
}
