package com.minio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MARKETING_TARGET_LIST_ITEM",
       uniqueConstraints = @UniqueConstraint(columnNames = {"MARKETING_TARGET_ID", "SMID"}),
       indexes = @Index(name = "IDX_TARGET_LIST_ITEM_SMID_TARGET_ID", columnList = "SMID,MARKETING_TARGET_ID"))
public class MarketingTargetListItem extends ABase {
    
    @Column(name = "MARKETING_TARGET_ID", nullable = false)
    private Long marketingTargetId;
    
    @Column(name = "SMID", nullable = false)
    private String smid;
    
    // Getters and setters
    
    public Long getMarketingTargetId() {
        return marketingTargetId;
    }
    
    public void setMarketingTargetId(Long marketingTargetId) {
        this.marketingTargetId = marketingTargetId;
    }
    
    public String getSmid() {
        return smid;
    }
    
    public void setSmid(String smid) {
        this.smid = smid;
    }
}
