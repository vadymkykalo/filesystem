package com.minio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MARKETING_TARGET_FILTER_CONDITION")
public class MarketingTargetFilterCondition extends ABase {
    
    @Column(name = "MARKETING_TARGET_FILTER_ID", nullable = false)
    private Long marketingTargetFilterId;
    
    @Column(name = "GROUP_ID")
    private Long groupId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "FIELD_TYPE", length = 50, nullable = false)
    private FilterFieldType fieldType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "OPERATOR", length = 10, nullable = false)
    private FilterOperator operator;
    
    @Column(name = "FIELD_VALUE", length = 1000, nullable = false)
    private String fieldValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "LOGICAL_OPERATOR", length = 10)
    private LogicalOperator logicalOperator;
    
    @Column(name = "ORDER_INDEX", nullable = false)
    private Integer orderIndex;
    
    // Getters and setters
    
    public Long getMarketingTargetFilterId() {
        return marketingTargetFilterId;
    }
    
    public void setMarketingTargetFilterId(Long marketingTargetFilterId) {
        this.marketingTargetFilterId = marketingTargetFilterId;
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
