package com.minio.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "MARKETING_TARGET_FILTER_GROUP")
public class MarketingTargetFilterGroup extends ABase {
    
    @Column(name = "MARKETING_TARGET_FILTER_ID", nullable = false)
    private Long marketingTargetFilterId;
    
    @Column(name = "GROUP_NAME", length = 255)
    private String groupName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "LOGICAL_OPERATOR", length = 10, nullable = false)
    private LogicalOperator logicalOperator;
    
    @Column(name = "ORDER_INDEX", nullable = false)
    private Integer orderIndex;
    
    @OneToMany(mappedBy = "groupId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MarketingTargetFilterCondition> conditions;
    
    // Getters and setters
    
    public Long getMarketingTargetFilterId() {
        return marketingTargetFilterId;
    }
    
    public void setMarketingTargetFilterId(Long marketingTargetFilterId) {
        this.marketingTargetFilterId = marketingTargetFilterId;
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
    
    public List<MarketingTargetFilterCondition> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<MarketingTargetFilterCondition> conditions) {
        this.conditions = conditions;
    }
}
