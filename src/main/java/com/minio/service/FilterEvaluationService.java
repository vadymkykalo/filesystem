package com.minio.service;

import com.minio.dto.UserRequestDto;
import com.minio.dto.MarketingTargetFilterDto;
import com.minio.dto.FilterConditionDto;
import com.minio.dto.FilterGroupDto;
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
        
        // Evaluate root-level conditions with proper logical operators
        if (filter.getConditions() != null && !filter.getConditions().isEmpty()) {
            if (!evaluateConditionsWithLogicalOperators(filter.getConditions(), userRequest)) {
                return false;
            }
        }
        
        // Check groups with their logical operators
        if (filter.getGroups() != null && !filter.getGroups().isEmpty()) {
            if (!evaluateGroupsWithLogicalOperators(filter.getGroups(), userRequest)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Evaluate conditions with their individual logical operators
     */
    private boolean evaluateConditionsWithLogicalOperators(List<FilterConditionDto> conditions, UserRequestDto userRequest) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        
        System.out.println("DEBUG: evaluateConditionsWithLogicalOperators - processing " + conditions.size() + " conditions");
        
        // Start with the first condition result
        boolean result = evaluateCondition(conditions.get(0), userRequest);
        System.out.println("DEBUG: First condition result: " + result);
        
        // Keep track of the last non-null operator to handle null operators correctly
        // Initialize with the first condition's operator (this will be used for the second condition)
        LogicalOperator lastOperator = conditions.get(0).getLogicalOperator();
        if (lastOperator == null) {
            lastOperator = LogicalOperator.AND; // Default if first condition has no operator
        }
        System.out.println("DEBUG: Initial lastOperator from first condition: " + lastOperator);
        
        // Process remaining conditions with their logical operators
        for (int i = 1; i < conditions.size(); i++) {
            FilterConditionDto condition = conditions.get(i);
            boolean conditionResult = evaluateCondition(condition, userRequest);
            System.out.println("DEBUG: Condition " + i + " result: " + conditionResult + " (operator: " + condition.getLogicalOperator() + ")");
            
            // The logical operator of the current condition determines how it combines with the previous result
            LogicalOperator operator = condition.getLogicalOperator();
            if (operator == null) {
                // If current operator is null, use the last non-null operator we've seen
                operator = lastOperator;
                System.out.println("DEBUG: Using last seen operator: " + operator);
            } else {
                // Update last operator for future null operators
                lastOperator = operator;
                System.out.println("DEBUG: Updated lastOperator to: " + operator);
            }
            
            if (operator == LogicalOperator.OR) {
                result = result || conditionResult;
                System.out.println("DEBUG: Applied OR: " + result);
            } else {
                result = result && conditionResult;
                System.out.println("DEBUG: Applied AND: " + result);
            }
        }
        
        System.out.println("DEBUG: Final evaluateConditionsWithLogicalOperators result: " + result);
        return result;
    }
    
    /**
     * Evaluate groups with their individual logical operators
     */
    private boolean evaluateGroupsWithLogicalOperators(List<FilterGroupDto> groups, UserRequestDto userRequest) {
        if (groups == null || groups.isEmpty()) {
            return true;
        }
        
        // Start with the first group result
        boolean result = evaluateGroupConditions(groups.get(0).getConditions(), userRequest);
        
        // Process remaining groups with their logical operators
        for (int i = 1; i < groups.size(); i++) {
            FilterGroupDto group = groups.get(i);
            boolean groupResult = evaluateGroupConditions(group.getConditions(), userRequest);
            
            // Use the logical operator of the current group to combine with previous result
            LogicalOperator operator = group.getLogicalOperator();
            if (operator == LogicalOperator.OR) {
                result = result || groupResult;
            } else {
                // Default to AND if operator is null or AND
                result = result && groupResult;
            }
        }
        
        return result;
    }
    
    /**
     * Evaluate conditions within a group using their individual logical operators
     */
    private boolean evaluateGroupConditions(List<FilterConditionDto> conditions, UserRequestDto userRequest) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        
        System.out.println("DEBUG: evaluateGroupConditions - conditions count: " + conditions.size());
        for (int i = 0; i < conditions.size(); i++) {
            FilterConditionDto condition = conditions.get(i);
            System.out.println("DEBUG: Condition " + i + ": " + condition.getFieldType() + " " + condition.getOperator() + " '" + condition.getFieldValue() + "' logicalOp=" + condition.getLogicalOperator());
        }
        
        // Evaluate conditions within the group using their individual logical operators
        boolean result = evaluateConditionsWithLogicalOperators(conditions, userRequest);
        System.out.println("DEBUG: evaluateGroupConditions result: " + result);
        return result;
    }
    
    /**
     * Evaluate individual condition
     */
    private boolean evaluateCondition(FilterConditionDto condition, UserRequestDto userRequest) {
        String fieldValue = userRequest.getFieldValue(condition.getFieldType().name());
        String conditionValue = condition.getFieldValue();
        FilterOperator operator = condition.getOperator();
        
        System.out.println("DEBUG: evaluateCondition - " + condition.getFieldType() + " " + operator + " '" + conditionValue + "'");
        System.out.println("DEBUG: User field value: '" + fieldValue + "'");
        
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
