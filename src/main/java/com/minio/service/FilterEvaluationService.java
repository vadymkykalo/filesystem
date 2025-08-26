package com.minio.service;

import com.minio.dto.UserRequestDto;
import com.minio.dto.MarketingTargetFilterDto;
import com.minio.dto.FilterConditionDto;
import com.minio.model.FilterOperator;
import com.minio.model.FilterFieldType;
import com.minio.model.LogicalOperator;
import com.minio.repository.MarketingTargetListItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service for evaluating user compliance with targeting filters
 */
@ApplicationScoped
public class FilterEvaluationService {
    
    @Inject
    MarketingTargetFilterService filterService;
    
    @Inject
    MarketingTargetListItemRepository listItemRepository;
    
    /**
     * Check if user matches the filter by filter ID
     */
    public boolean evaluateFilter(Long filterId, UserRequestDto userRequest) {
        try {
            Optional<MarketingTargetFilterDto> filterOpt = filterService.getFilterById(filterId);
            return filterOpt.filter(filterDto -> evaluateFilter(filterDto, userRequest)).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if user matches the filter
     */
    public boolean evaluateFilter(MarketingTargetFilterDto filter, UserRequestDto userRequest) {
        if (filter == null || !filter.getIsActive()) {
            return false;
        }
        
        // Simple evaluation: check all conditions with AND logic
        if (filter.getConditions() != null && !filter.getConditions().isEmpty()) {
            for (FilterConditionDto condition : filter.getConditions()) {
                if (!evaluateCondition(condition, userRequest)) {
                    return false;
                }
            }
        }
        
        // Check groups with their logical operators
        if (filter.getGroups() != null && !filter.getGroups().isEmpty()) {
            // For now, simple AND logic between groups
            for (var group : filter.getGroups()) {
                if (group.getConditions() != null && !group.getConditions().isEmpty()) {
                    boolean groupResult = evaluateGroupConditions(group.getConditions(), group.getLogicalOperator(), userRequest);
                    if (!groupResult) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Evaluate group conditions with logical operator
     */
    private boolean evaluateGroupConditions(List<FilterConditionDto> conditions, LogicalOperator operator, UserRequestDto userRequest) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        
        if (operator == LogicalOperator.OR) {
            // At least one condition must be true
            for (FilterConditionDto condition : conditions) {
                if (evaluateCondition(condition, userRequest)) {
                    return true;
                }
            }
            return false;
        } else {
            // Default AND logic - all conditions must be true
            for (FilterConditionDto condition : conditions) {
                if (!evaluateCondition(condition, userRequest)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    /**
     * Evaluate individual condition
     */
    private boolean evaluateCondition(FilterConditionDto condition, UserRequestDto userRequest) {
        String fieldValue = userRequest.getFieldValue(condition.getFieldType().name());
        String conditionValue = condition.getFieldValue();
        FilterOperator operator = condition.getOperator();
        
        // Special handling for distribution group files
        if (condition.getFieldType() == FilterFieldType.DISTRIBUTION_GROUPS_FILE) {
            return evaluateDistributionGroupsFile(condition, userRequest);
        }
        
        // If field value is empty, condition is not met (except IS_NULL/IS_NOT_NULL)
        if (fieldValue == null) {
            return operator == FilterOperator.IS_NULL;
        }
        
        if (conditionValue == null) {
            return operator == FilterOperator.IS_NOT_NULL;
        }
        
        switch (operator) {
            case EQUAL:
                return fieldValue.equals(conditionValue);
                
            case NOT_EQUAL:
                return !fieldValue.equals(conditionValue);
                
            case CONTAINS:
                return fieldValue.toLowerCase().contains(conditionValue.toLowerCase());
                
            case NOT_CONTAINS:
                return !fieldValue.toLowerCase().contains(conditionValue.toLowerCase());
                
            case STARTS_WITH:
                return fieldValue.toLowerCase().startsWith(conditionValue.toLowerCase());
                
            case ENDS_WITH:
                return fieldValue.toLowerCase().endsWith(conditionValue.toLowerCase());
                
            case GREATER_THAN:
                return compareNumeric(fieldValue, conditionValue) > 0;
                
            case GREATER_THAN_OR_EQUAL:
                return compareNumeric(fieldValue, conditionValue) >= 0;
                
            case LESS_THAN:
                return compareNumeric(fieldValue, conditionValue) < 0;
                
            case LESS_THAN_OR_EQUAL:
                return compareNumeric(fieldValue, conditionValue) <= 0;
                
            case IN:
                String[] values = conditionValue.split(",");
                for (String value : values) {
                    if (fieldValue.equals(value.trim())) {
                        return true;
                    }
                }
                return false;
                
            case NOT_IN:
                String[] notValues = conditionValue.split(",");
                for (String value : notValues) {
                    if (fieldValue.equals(value.trim())) {
                        return false;
                    }
                }
                return true;
                
            case REGEX:
                try {
                    Pattern pattern = Pattern.compile(conditionValue);
                    return pattern.matcher(fieldValue).matches();
                } catch (Exception e) {
                    return false;
                }
                
            case IS_NULL:
                return false; // fieldValue already checked above
                
            case IS_NOT_NULL:
                return true; // fieldValue already checked above
                
            default:
                return false;
        }
    }
    
    /**
     * Check distribution group files (DISTRIBUTION_GROUPS_FILE)
     */
    private boolean evaluateDistributionGroupsFile(FilterConditionDto condition, UserRequestDto userRequest) {
        String smid = userRequest.getSmid();
        if (smid == null) {
            return false;
        }
        
        String fileId = condition.getFieldValue();
        FilterOperator operator = condition.getOperator();

        boolean existsInFile = listItemRepository.existsBySmidAndFileId(smid, fileId);

        return switch (operator) {
            case IN -> existsInFile;
            case NOT_IN -> !existsInFile;
            default -> false;
        };
    }
    
    /**
     * Compare numeric values
     */
    private int compareNumeric(String value1, String value2) {
        try {
            Double num1 = Double.parseDouble(value1);
            Double num2 = Double.parseDouble(value2);
            return num1.compareTo(num2);
        } catch (NumberFormatException e) {
            // If not numbers, compare as strings
            return value1.compareTo(value2);
        }
    }
}
