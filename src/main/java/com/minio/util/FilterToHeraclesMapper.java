package com.minio.util;

import com.minio.dto.FilterConditionDto;
import com.minio.dto.FilterGroupDto;
import com.minio.dto.MarketingTargetFilterDto;

/**
 * Mapper for packing MarketingTargetFilterDto into flat Heracles structure
 * Unpacks complex nested structure into simple key-value pairs
 */
public class FilterToHeraclesMapper {
    
    /**
     * Pack MarketingTargetFilterDto into flat Heracles
     * All nested objects are flattened with prefixed keys
     */
    public static Heracles pack(MarketingTargetFilterDto filter) throws PanteonException {
        if (filter == null) {
            return new Heracles();
        }
        
        Heracles heracles = new Heracles();
        
        // Pack main filter fields
        heracles.setValue("filter_id", filter.getId());
        heracles.setValue("filter_marketingTargetId", filter.getMarketingTargetId());
        heracles.setValue("filter_name", filter.getFilterName());
        heracles.setValue("filter_description", filter.getDescription());
        heracles.setValue("filter_isActive", filter.getIsActive());
        
        // Pack root conditions
        if (filter.getConditions() != null) {
            heracles.setValue("conditions_count", filter.getConditions().size());
            for (int i = 0; i < filter.getConditions().size(); i++) {
                packCondition(heracles, filter.getConditions().get(i), "condition_" + i + "_");
            }
        } else {
            heracles.setValue("conditions_count", 0);
        }
        
        // Pack groups
        if (filter.getGroups() != null) {
            heracles.setValue("groups_count", filter.getGroups().size());
            for (int i = 0; i < filter.getGroups().size(); i++) {
                packGroup(heracles, filter.getGroups().get(i), "group_" + i + "_");
            }
        } else {
            heracles.setValue("groups_count", 0);
        }
        
        return heracles;
    }
    
    /**
     * Pack single condition with prefix
     */
    private static void packCondition(Heracles heracles, FilterConditionDto condition, String prefix) throws PanteonException {
        heracles.setValue(prefix + "id", condition.getId());
        heracles.setValue(prefix + "groupId", condition.getGroupId());
        heracles.setValue(prefix + "fieldType", condition.getFieldType() != null ? condition.getFieldType().name() : null);
        heracles.setValue(prefix + "operator", condition.getOperator() != null ? condition.getOperator().name() : null);
        heracles.setValue(prefix + "fieldValue", condition.getFieldValue());
        heracles.setValue(prefix + "logicalOperator", condition.getLogicalOperator() != null ? condition.getLogicalOperator().name() : null);
        heracles.setValue(prefix + "orderIndex", condition.getOrderIndex());
    }
    
    /**
     * Pack single group with all its conditions
     */
    private static void packGroup(Heracles heracles, FilterGroupDto group, String prefix) throws PanteonException {
        heracles.setValue(prefix + "id", group.getId());
        heracles.setValue(prefix + "name", group.getGroupName());
        heracles.setValue(prefix + "logicalOperator", group.getLogicalOperator() != null ? group.getLogicalOperator().name() : null);
        heracles.setValue(prefix + "orderIndex", group.getOrderIndex());
        
        // Pack group conditions
        if (group.getConditions() != null) {
            heracles.setValue(prefix + "conditions_count", group.getConditions().size());
            for (int i = 0; i < group.getConditions().size(); i++) {
                packCondition(heracles, group.getConditions().get(i), prefix + "condition_" + i + "_");
            }
        } else {
            heracles.setValue(prefix + "conditions_count", 0);
        }
    }
    
    /**
     * Pack filter with command for module communication
     */
    public static Heracles packWithCommand(String command, MarketingTargetFilterDto filter) throws PanteonException {
        Heracles heracles = pack(filter);
        heracles.setValue("command", command);
        return heracles;
    }
}
