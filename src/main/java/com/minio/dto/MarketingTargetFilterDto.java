package com.minio.dto;

import java.util.List;

public class MarketingTargetFilterDto {
    
    private Long id;
    private Long marketingTargetId;
    private String filterName;
    private String description;
    private Boolean isActive;
    private List<FilterConditionDto> conditions;
    private List<FilterGroupDto> groups;
    
    // Constructors
    public MarketingTargetFilterDto() {}
    
    public MarketingTargetFilterDto(String filterName, Long marketingTargetId) {
        this.filterName = filterName;
        this.marketingTargetId = marketingTargetId;
        this.isActive = true;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public List<FilterConditionDto> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<FilterConditionDto> conditions) {
        this.conditions = conditions;
    }
    
    public List<FilterGroupDto> getGroups() {
        return groups;
    }
    
    public void setGroups(List<FilterGroupDto> groups) {
        this.groups = groups;
    }
}
