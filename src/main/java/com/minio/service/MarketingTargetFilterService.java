package com.minio.service;

import com.minio.dto.FilterConditionDto;
import com.minio.dto.FilterGroupDto;
import com.minio.dto.MarketingTargetFilterDto;
import com.minio.model.*;
import com.minio.repository.MarketingTargetFilterRepository;
import com.minio.repository.MarketingTargetFilterConditionRepository;
import com.minio.repository.MarketingTargetFilterGroupRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class MarketingTargetFilterService {
    
    @Inject
    MarketingTargetFilterRepository filterRepository;
    
    @Inject
    MarketingTargetFilterConditionRepository conditionRepository;
    
    @Inject
    MarketingTargetFilterGroupRepository groupRepository;
    
    public List<MarketingTargetFilterDto> getAllFilters() {
        return filterRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public List<MarketingTargetFilterDto> getFiltersByMarketingTargetId(Long marketingTargetId) {
        return filterRepository.findByMarketingTargetId(marketingTargetId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public Optional<MarketingTargetFilterDto> getFilterById(Long id) {
        return filterRepository.findByIdWithConditions(id)
            .map(this::convertToDtoWithDetails);
    }
    
    @Transactional
    public MarketingTargetFilterDto createFilter(MarketingTargetFilterDto filterDto) {
        validateFilterStructure(filterDto);
        validateFilterName(filterDto.getFilterName(), null);
        
        MarketingTargetFilter filter = convertToEntity(filterDto);
        filter = filterRepository.save(filter);
        
        // Save conditions
        if (filterDto.getConditions() != null) {
            for (int i = 0; i < filterDto.getConditions().size(); i++) {
                FilterConditionDto conditionDto = filterDto.getConditions().get(i);
                MarketingTargetFilterCondition condition = convertConditionToEntity(conditionDto);
                condition.setMarketingTargetFilterId(filter.getId());
                condition.setOrderIndex(i);
                conditionRepository.save(condition);
            }
        }
        
        // Save groups
        if (filterDto.getGroups() != null) {
            for (int i = 0; i < filterDto.getGroups().size(); i++) {
                FilterGroupDto groupDto = filterDto.getGroups().get(i);
                MarketingTargetFilterGroup group = convertGroupToEntity(groupDto);
                group.setMarketingTargetFilterId(filter.getId());
                group.setOrderIndex(i);
                group = groupRepository.save(group);
                
                // Save group conditions
                if (groupDto.getConditions() != null) {
                    for (int j = 0; j < groupDto.getConditions().size(); j++) {
                        FilterConditionDto conditionDto = groupDto.getConditions().get(j);
                        MarketingTargetFilterCondition condition = convertConditionToEntity(conditionDto);
                        condition.setMarketingTargetFilterId(filter.getId());
                        condition.setGroupId(group.getId());
                        condition.setOrderIndex(j);
                        conditionRepository.save(condition);
                    }
                }
            }
        }
        
        return getFilterById(filter.getId()).orElse(null);
    }
    
    @Transactional
    public MarketingTargetFilterDto updateFilter(Long id, MarketingTargetFilterDto filterDto) {
        Optional<MarketingTargetFilter> existingFilter = filterRepository.findById(id);
        if (existingFilter.isEmpty()) {
            throw new RuntimeException("Filter not found with id: " + id);
        }
        
        validateFilterStructure(filterDto);
        validateFilterName(filterDto.getFilterName(), id);
        
        // Update main filter
        MarketingTargetFilter filter = existingFilter.get();
        filter.setFilterName(filterDto.getFilterName());
        filter.setDescription(filterDto.getDescription());
        filter.setIsActive(filterDto.getIsActive());
        filter = filterRepository.save(filter);
        
        // Delete existing conditions and groups
        conditionRepository.deleteByFilterId(id);
        groupRepository.deleteByFilterId(id);
        
        // Save new conditions
        if (filterDto.getConditions() != null) {
            for (int i = 0; i < filterDto.getConditions().size(); i++) {
                FilterConditionDto conditionDto = filterDto.getConditions().get(i);
                MarketingTargetFilterCondition condition = convertConditionToEntity(conditionDto);
                condition.setMarketingTargetFilterId(filter.getId());
                condition.setOrderIndex(i);
                conditionRepository.save(condition);
            }
        }
        
        // Save new groups
        if (filterDto.getGroups() != null) {
            for (int i = 0; i < filterDto.getGroups().size(); i++) {
                FilterGroupDto groupDto = filterDto.getGroups().get(i);
                MarketingTargetFilterGroup group = convertGroupToEntity(groupDto);
                group.setMarketingTargetFilterId(filter.getId());
                group.setOrderIndex(i);
                group = groupRepository.save(group);
                
                // Save group conditions
                if (groupDto.getConditions() != null) {
                    for (int j = 0; j < groupDto.getConditions().size(); j++) {
                        FilterConditionDto conditionDto = groupDto.getConditions().get(j);
                        MarketingTargetFilterCondition condition = convertConditionToEntity(conditionDto);
                        condition.setMarketingTargetFilterId(filter.getId());
                        condition.setGroupId(group.getId());
                        condition.setOrderIndex(j);
                        conditionRepository.save(condition);
                    }
                }
            }
        }
        
        return getFilterById(filter.getId()).orElse(null);
    }
    
    @Transactional
    public void deleteFilter(Long id) {
        conditionRepository.deleteByFilterId(id);
        groupRepository.deleteByFilterId(id);
        filterRepository.deleteById(id);
    }
    
    private void validateFilterName(String filterName, Long excludeId) {
        boolean exists = excludeId == null 
            ? filterRepository.existsByFilterName(filterName)
            : filterRepository.existsByFilterNameAndIdNot(filterName, excludeId);
        
        if (exists) {
            throw new RuntimeException("Filter with name '" + filterName + "' already exists");
        }
    }
    
    /**
     * Validate filter structure
     */
    public void validateFilterStructure(MarketingTargetFilterDto filterDto) {
        if (filterDto == null) {
            throw new RuntimeException("Filter cannot be null");
        }
        
        // Check required fields
        if (filterDto.getFilterName() == null || filterDto.getFilterName().trim().isEmpty()) {
            throw new RuntimeException("Filter name is required");
        }
        
        if (filterDto.getMarketingTargetId() == null) {
            throw new RuntimeException("Marketing target ID is required");
        }
        
        // Check that there is at least one condition or group
        boolean hasConditions = filterDto.getConditions() != null && !filterDto.getConditions().isEmpty();
        boolean hasGroups = filterDto.getGroups() != null && !filterDto.getGroups().isEmpty();
        
        if (!hasConditions && !hasGroups) {
            throw new RuntimeException("Filter must have at least one condition or group");
        }
        
        // Validate root level conditions
        if (hasConditions) {
            validateConditions(filterDto.getConditions(), "root level");
        }
        
        // Validate groups
        if (hasGroups) {
            validateGroups(filterDto.getGroups());
        }
    }
    
    private void validateConditions(List<FilterConditionDto> conditions, String context) {
        if (conditions == null || conditions.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < conditions.size(); i++) {
            FilterConditionDto condition = conditions.get(i);
            
            if (condition.getFieldType() == null) {
                throw new RuntimeException("Field type is required for condition " + i + " at " + context);
            }
            
            if (condition.getOperator() == null) {
                throw new RuntimeException("Operator is required for condition " + i + " at " + context);
            }
            
            // Check field value (except for IS_NULL and IS_NOT_NULL)
            if (condition.getOperator() != FilterOperator.IS_NULL && 
                condition.getOperator() != FilterOperator.IS_NOT_NULL) {
                if (condition.getFieldValue() == null || condition.getFieldValue().trim().isEmpty()) {
                    throw new RuntimeException("Field value is required for condition " + i + " at " + context + " with operator " + condition.getOperator());
                }
            }
            
            // Special validation for distribution group files
            if (condition.getFieldType() == FilterFieldType.DISTRIBUTION_GROUPS_FILE) {
                if (condition.getOperator() != FilterOperator.IN && 
                    condition.getOperator() != FilterOperator.NOT_IN &&
                    condition.getOperator() != FilterOperator.EQUAL &&
                    condition.getOperator() != FilterOperator.NOT_EQUAL) {
                    throw new RuntimeException("DISTRIBUTION_GROUPS_FILE supports only IN, NOT_IN, EQUAL, NOT_EQUAL operators");
                }
            }
            
            // Version validation for numeric operators
            if (condition.getFieldType() == FilterFieldType.CLIENT_VERSION && 
                (condition.getOperator() == FilterOperator.GREATER_THAN ||
                 condition.getOperator() == FilterOperator.GREATER_THAN_OR_EQUAL ||
                 condition.getOperator() == FilterOperator.LESS_THAN ||
                 condition.getOperator() == FilterOperator.LESS_THAN_OR_EQUAL)) {
                if (!isValidVersion(condition.getFieldValue())) {
                    throw new RuntimeException("Invalid version format for condition " + i + " at " + context + ": " + condition.getFieldValue());
                }
            }
        }
    }
    
    private void validateGroups(List<FilterGroupDto> groups) {
        if (groups == null || groups.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < groups.size(); i++) {
            FilterGroupDto group = groups.get(i);
            
            if (group.getGroupName() == null || group.getGroupName().trim().isEmpty()) {
                throw new RuntimeException("Group name is required for group " + i);
            }
            
            if (group.getLogicalOperator() == null) {
                throw new RuntimeException("Logical operator is required for group " + i + " (" + group.getGroupName() + ")");
            }
            
            if (group.getConditions() == null || group.getConditions().isEmpty()) {
                throw new RuntimeException("Group " + i + " (" + group.getGroupName() + ") must have at least one condition");
            }
            
            // Validate conditions in group
            validateConditions(group.getConditions(), "group '" + group.getGroupName() + "'");
        }
    }
    
    private boolean isValidVersion(String version) {
        if (version == null || version.trim().isEmpty()) {
            return false;
        }
        
        // Simple version format check (e.g.: 1.0.0, 2.1.5, 10.15.7)
        return version.matches("^\\d+(\\.\\d+)*$");
    }
    
    private MarketingTargetFilterDto convertToDto(MarketingTargetFilter filter) {
        MarketingTargetFilterDto dto = new MarketingTargetFilterDto();
        dto.setId(filter.getId());
        dto.setMarketingTargetId(filter.getMarketingTargetId());
        dto.setFilterName(filter.getFilterName());
        dto.setDescription(filter.getDescription());
        dto.setIsActive(filter.getIsActive());
        return dto;
    }
    
    private MarketingTargetFilterDto convertToDtoWithDetails(MarketingTargetFilter filter) {
        MarketingTargetFilterDto dto = convertToDto(filter);
        
        // Convert only root-level conditions (conditions that don't belong to any group)
        if (filter.getConditions() != null) {
            dto.setConditions(filter.getConditions().stream()
                .filter(condition -> condition.getGroupId() == null) // Only root-level conditions
                .sorted((c1, c2) -> Integer.compare(c1.getOrderIndex(), c2.getOrderIndex())) // Sort by orderIndex
                .map(this::convertConditionToDto)
                .collect(Collectors.toList()));
        }
        
        // Convert groups
        if (filter.getGroups() != null) {
            dto.setGroups(filter.getGroups().stream()
                .sorted((g1, g2) -> Integer.compare(g1.getOrderIndex(), g2.getOrderIndex())) // Sort by orderIndex
                .map(this::convertGroupToDto)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private MarketingTargetFilter convertToEntity(MarketingTargetFilterDto dto) {
        MarketingTargetFilter filter = new MarketingTargetFilter();
        filter.setMarketingTargetId(dto.getMarketingTargetId());
        filter.setFilterName(dto.getFilterName());
        filter.setDescription(dto.getDescription());
        filter.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return filter;
    }
    
    private FilterConditionDto convertConditionToDto(MarketingTargetFilterCondition condition) {
        FilterConditionDto dto = new FilterConditionDto();
        dto.setId(condition.getId());
        dto.setGroupId(condition.getGroupId());
        dto.setFieldType(condition.getFieldType());
        dto.setOperator(condition.getOperator());
        dto.setFieldValue(condition.getFieldValue());
        dto.setLogicalOperator(condition.getLogicalOperator());
        dto.setOrderIndex(condition.getOrderIndex());
        return dto;
    }
    
    private MarketingTargetFilterCondition convertConditionToEntity(FilterConditionDto dto) {
        MarketingTargetFilterCondition condition = new MarketingTargetFilterCondition();
        condition.setFieldType(dto.getFieldType());
        condition.setOperator(dto.getOperator());
        condition.setFieldValue(dto.getFieldValue());
        condition.setLogicalOperator(dto.getLogicalOperator());
        return condition;
    }
    
    private FilterGroupDto convertGroupToDto(MarketingTargetFilterGroup group) {
        FilterGroupDto dto = new FilterGroupDto();
        dto.setId(group.getId());
        dto.setGroupName(group.getGroupName());
        dto.setLogicalOperator(group.getLogicalOperator());
        dto.setOrderIndex(group.getOrderIndex());
        
        if (group.getConditions() != null) {
            dto.setConditions(group.getConditions().stream()
                .sorted((c1, c2) -> Integer.compare(c1.getOrderIndex(), c2.getOrderIndex())) // Sort by orderIndex
                .map(this::convertConditionToDto)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private MarketingTargetFilterGroup convertGroupToEntity(FilterGroupDto dto) {
        MarketingTargetFilterGroup group = new MarketingTargetFilterGroup();
        group.setGroupName(dto.getGroupName());
        group.setLogicalOperator(dto.getLogicalOperator());
        return group;
    }
}
