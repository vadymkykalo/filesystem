package com.minio.util;

import com.minio.dto.FilterConditionDto;
import com.minio.dto.FilterGroupDto;
import com.minio.dto.MarketingTargetFilterDto;
import com.minio.model.FilterFieldType;
import com.minio.model.FilterOperator;
import com.minio.model.LogicalOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for unpacking flat Heracles structure back to MarketingTargetFilterDto
 * Reconstructs complex nested structure from simple key-value pairs
 */
public class HeraclesToFilterMapper {
    
    /**
     * Unpack flat Heracles into MarketingTargetFilterDto
     * Reconstructs nested structure from prefixed keys
     */
    public static MarketingTargetFilterDto unpack(Heracles heracles) throws PanteonException {
        if (heracles == null || heracles.isEmpty()) {
            return null;
        }
        
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        
        // Unpack main filter fields
        filter.setId(heracles.getValueDef("filter_id", null));
        filter.setMarketingTargetId(heracles.getValueDef("filter_marketingTargetId", null));
        filter.setFilterName(heracles.getValueDef("filter_name", null));
        filter.setDescription(heracles.getValueDef("filter_description", null));
        filter.setIsActive(heracles.getValueDef("filter_isActive", null));
        
        // Unpack root conditions
        Integer conditionsCount = heracles.getValueDef("conditions_count", 0);
        if (conditionsCount > 0) {
            List<FilterConditionDto> conditions = new ArrayList<>();
            for (int i = 0; i < conditionsCount; i++) {
                FilterConditionDto condition = unpackCondition(heracles, "condition_" + i + "_");
                if (condition != null) {
                    conditions.add(condition);
                }
            }
            filter.setConditions(conditions);
        }
        
        // Unpack groups
        Integer groupsCount = heracles.getValueDef("groups_count", 0);
        if (groupsCount > 0) {
            List<FilterGroupDto> groups = new ArrayList<>();
            for (int i = 0; i < groupsCount; i++) {
                FilterGroupDto group = unpackGroup(heracles, "group_" + i + "_");
                if (group != null) {
                    groups.add(group);
                }
            }
            filter.setGroups(groups);
        }
        
        return filter;
    }
    
    /**
     * Unpack single condition from prefixed keys
     */
    private static FilterConditionDto unpackCondition(Heracles heracles, String prefix) throws PanteonException {
        // Check if condition exists
        if (!heracles.hasValue(prefix + "id") && !heracles.hasValue(prefix + "fieldType")) {
            return null;
        }
        
        FilterConditionDto condition = new FilterConditionDto();
        
        condition.setId(heracles.getValueDef(prefix + "id", null));
        condition.setGroupId(heracles.getValueDef(prefix + "groupId", null));
        
        // Unpack enums safely
        String fieldTypeStr = heracles.getValueDef(prefix + "fieldType", null);
        if (fieldTypeStr != null) {
            try {
                condition.setFieldType(FilterFieldType.valueOf(fieldTypeStr));
            } catch (IllegalArgumentException e) {
                // Handle invalid enum value
                condition.setFieldType(null);
            }
        }
        
        String operatorStr = heracles.getValueDef(prefix + "operator", null);
        if (operatorStr != null) {
            try {
                condition.setOperator(FilterOperator.valueOf(operatorStr));
            } catch (IllegalArgumentException e) {
                condition.setOperator(null);
            }
        }
        
        String logicalOperatorStr = heracles.getValueDef(prefix + "logicalOperator", null);
        if (logicalOperatorStr != null) {
            try {
                condition.setLogicalOperator(LogicalOperator.valueOf(logicalOperatorStr));
            } catch (IllegalArgumentException e) {
                condition.setLogicalOperator(null);
            }
        }
        
        condition.setFieldValue(heracles.getValueDef(prefix + "fieldValue", null));
        condition.setOrderIndex(heracles.getValueDef(prefix + "orderIndex", null));
        
        return condition;
    }
    
    /**
     * Unpack single group with all its conditions
     */
    private static FilterGroupDto unpackGroup(Heracles heracles, String prefix) throws PanteonException {
        // Check if group exists
        if (!heracles.hasValue(prefix + "id") && !heracles.hasValue(prefix + "name")) {
            return null;
        }
        
        FilterGroupDto group = new FilterGroupDto();
        
        group.setId(heracles.getValueDef(prefix + "id", null));
        group.setGroupName(heracles.getValueDef(prefix + "name", null));
        group.setOrderIndex(heracles.getValueDef(prefix + "orderIndex", null));
        
        // Unpack logical operator
        String logicalOperatorStr = heracles.getValueDef(prefix + "logicalOperator", null);
        if (logicalOperatorStr != null) {
            try {
                group.setLogicalOperator(LogicalOperator.valueOf(logicalOperatorStr));
            } catch (IllegalArgumentException e) {
                group.setLogicalOperator(null);
            }
        }
        
        // Unpack group conditions
        Integer groupConditionsCount = heracles.getValueDef(prefix + "conditions_count", 0);
        if (groupConditionsCount > 0) {
            List<FilterConditionDto> groupConditions = new ArrayList<>();
            for (int i = 0; i < groupConditionsCount; i++) {
                FilterConditionDto condition = unpackCondition(heracles, prefix + "condition_" + i + "_");
                if (condition != null) {
                    groupConditions.add(condition);
                }
            }
            group.setConditions(groupConditions);
        }
        
        return group;
    }
    
    /**
     * Get command from Heracles if it exists
     */
    public static String getCommand(Heracles heracles) throws PanteonException {
        return heracles.getValueDef("command", null);
    }
    
    /**
     * Check if Heracles contains filter data
     */
    public static boolean hasFilterData(Heracles heracles) {
        return heracles != null && 
               (heracles.hasValue("filter_name") || 
                heracles.hasValue("filter_id") || 
                heracles.hasValue("conditions_count") || 
                heracles.hasValue("groups_count"));
    }
    
    /**
     * Validate unpacked filter
     */
    public static boolean validateFilter(MarketingTargetFilterDto filter) {
        if (filter == null) {
            return false;
        }
        
        // Basic validation - filter must have a name
        if (filter.getFilterName() == null || filter.getFilterName().trim().isEmpty()) {
            return false;
        }
        
        // Validate conditions if present
        if (filter.getConditions() != null) {
            for (FilterConditionDto condition : filter.getConditions()) {
                if (condition.getFieldType() == null || condition.getOperator() == null) {
                    return false;
                }
            }
        }
        
        // Validate groups if present
        if (filter.getGroups() != null) {
            for (FilterGroupDto group : filter.getGroups()) {
                if (group.getConditions() != null) {
                    for (FilterConditionDto condition : group.getConditions()) {
                        if (condition.getFieldType() == null || condition.getOperator() == null) {
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }
}
