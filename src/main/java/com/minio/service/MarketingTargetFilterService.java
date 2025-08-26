package com.minio.service;

import com.minio.dto.FilterConditionDto;
import com.minio.dto.FilterGroupDto;
import com.minio.dto.MarketingTargetFilterDto;
import com.minio.model.LogicalOperator;
import com.minio.model.MarketingTargetFilter;
import com.minio.model.MarketingTargetFilterCondition;
import com.minio.model.MarketingTargetFilterGroup;
import com.minio.repository.MarketingTargetFilterRepository;
import com.minio.repository.MarketingTargetFilterConditionRepository;
import com.minio.repository.MarketingTargetFilterGroupRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        
        // Convert conditions
        if (filter.getConditions() != null) {
            dto.setConditions(filter.getConditions().stream()
                .map(this::convertConditionToDto)
                .collect(Collectors.toList()));
        }
        
        // Convert groups
        if (filter.getGroups() != null) {
            dto.setGroups(filter.getGroups().stream()
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
