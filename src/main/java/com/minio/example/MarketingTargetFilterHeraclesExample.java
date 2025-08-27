package com.minio.example;

import com.minio.dto.MarketingTargetFilterDto;
import com.minio.util.Heracles;
import com.minio.util.FilterToHeraclesMapper;
import com.minio.util.HeraclesToFilterMapper;

/**
 * Simple example: pack MarketingTargetFilterDto into Heracles and unpack it back
 */
public class MarketingTargetFilterHeraclesExample {
    
    public static void main(String[] args) {
        try {
            // 1. Create filter
            MarketingTargetFilterDto filter = createFilter();
            
            // 2. PACK into Heracles
            Heracles heracles = FilterToHeraclesMapper.packWithCommand("CREATE_FILTER", filter);
            
            // 3. UNPACK from Heracles
            MarketingTargetFilterDto restoredFilter = HeraclesToFilterMapper.unpack(heracles);
            String command = HeraclesToFilterMapper.getCommand(heracles);
            
            System.out.println("Original: " + filter.getFilterName());
            System.out.println("Restored: " + restoredFilter.getFilterName());
            System.out.println("Command: " + command);
            System.out.println("Success: " + filter.getFilterName().equals(restoredFilter.getFilterName()));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static MarketingTargetFilterDto createFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setId(1L);
        filter.setFilterName("Complex UA Filter");
        filter.setDescription("Ukrainian users with specific conditions");
        filter.setIsActive(true);
        
        // Добавим условия и группы чтобы показать что дерево работает
        java.util.List<com.minio.dto.FilterConditionDto> conditions = new java.util.ArrayList<>();
        com.minio.dto.FilterConditionDto condition = new com.minio.dto.FilterConditionDto();
        condition.setId(1L);
        condition.setFieldType(com.minio.model.FilterFieldType.COUNTRY);
        condition.setOperator(com.minio.model.FilterOperator.EQUAL);
        condition.setFieldValue("UA");
        conditions.add(condition);
        filter.setConditions(conditions);
        
        java.util.List<com.minio.dto.FilterGroupDto> groups = new java.util.ArrayList<>();
        com.minio.dto.FilterGroupDto group = new com.minio.dto.FilterGroupDto();
        group.setId(1L);
        group.setGroupName("SMID Group");
        group.setLogicalOperator(com.minio.model.LogicalOperator.OR);
        
        java.util.List<com.minio.dto.FilterConditionDto> groupConditions = new java.util.ArrayList<>();
        com.minio.dto.FilterConditionDto smidCondition = new com.minio.dto.FilterConditionDto();
        smidCondition.setId(2L);
        smidCondition.setFieldType(com.minio.model.FilterFieldType.SMID);
        smidCondition.setOperator(com.minio.model.FilterOperator.EQUAL);
        smidCondition.setFieldValue("12345");
        groupConditions.add(smidCondition);
        group.setConditions(groupConditions);
        groups.add(group);
        filter.setGroups(groups);
        
        return filter;
    }
}
