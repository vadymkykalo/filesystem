package com.minio.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "MARKETING_TARGET_FILTER")
public class MarketingTargetFilter extends ABase {
    
    @Column(name = "MARKETING_TARGET_ID", nullable = false)
    private Long marketingTargetId;
    
    @Column(name = "FILTER_NAME", length = 255, nullable = false)
    private String filterName;
    
    @Column(name = "DESCRIPTION", length = 1000)
    private String description;
    
    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "marketingTargetFilterId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MarketingTargetFilterCondition> conditions;
    
    @OneToMany(mappedBy = "marketingTargetFilterId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MarketingTargetFilterGroup> groups;
    
    // Getters and setters
    
    public Long getMarketingTargetId() {
        return marketingTargetId;
    }
    
    public void setMarketingTargetId(Long marketingTargetId) {
        this.marketingTargetId = marketingTargetId;
    }
    
    public String getFilterName() {
        return filterName;
    }
    
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public List<MarketingTargetFilterCondition> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<MarketingTargetFilterCondition> conditions) {
        this.conditions = conditions;
    }
    
    public List<MarketingTargetFilterGroup> getGroups() {
        return groups;
    }
    
    public void setGroups(List<MarketingTargetFilterGroup> groups) {
        this.groups = groups;
    }
}
