package com.minio.dto;

import com.minio.model.LogicalOperator;
import java.util.List;

public class FilterGroupDto {
    
    private Long id;
    private String groupName;
    private LogicalOperator logicalOperator;
    private Integer orderIndex;
    private List<FilterConditionDto> conditions;
    
    // Constructors
    public FilterGroupDto() {}
    
    public FilterGroupDto(String groupName, LogicalOperator logicalOperator) {
        this.groupName = groupName;
        this.logicalOperator = logicalOperator;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }
    
    public void setLogicalOperator(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }
    
    public Integer getOrderIndex() {
        return orderIndex;
    }
    
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
    
    public List<FilterConditionDto> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<FilterConditionDto> conditions) {
        this.conditions = conditions;
    }
}
