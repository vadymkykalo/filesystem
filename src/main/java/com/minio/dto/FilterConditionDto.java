package com.minio.dto;

import com.minio.model.FilterFieldType;
import com.minio.model.FilterOperator;
import com.minio.model.LogicalOperator;

public class FilterConditionDto {
    
    private Long id;
    private Long groupId;
    private FilterFieldType fieldType;
    private FilterOperator operator;
    private String fieldValue;
    private LogicalOperator logicalOperator;
    private Integer orderIndex;
    
    // Constructors
    public FilterConditionDto() {}
    
    public FilterConditionDto(FilterFieldType fieldType, FilterOperator operator, String fieldValue) {
        this.fieldType = fieldType;
        this.operator = operator;
        this.fieldValue = fieldValue;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    public FilterFieldType getFieldType() {
        return fieldType;
    }
    
    public void setFieldType(FilterFieldType fieldType) {
        this.fieldType = fieldType;
    }
    
    public FilterOperator getOperator() {
        return operator;
    }
    
    public void setOperator(FilterOperator operator) {
        this.operator = operator;
    }
    
    public String getFieldValue() {
        return fieldValue;
    }
    
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
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
}
