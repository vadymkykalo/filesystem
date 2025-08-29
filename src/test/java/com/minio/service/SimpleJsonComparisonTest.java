package com.minio.service;

import com.minio.dto.MarketingTargetFilterDto;
import com.minio.dto.FilterConditionDto;
import com.minio.dto.FilterGroupDto;
import com.minio.dto.UserRequestDto;
import com.minio.model.FilterFieldType;
import com.minio.model.FilterOperator;
import com.minio.model.LogicalOperator;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Простий тест для порівняння правильного і неправильного JSON
 */
public class SimpleJsonComparisonTest {

    @Test
    public void compareCorrectVsIncorrectJson() {
        FilterEvaluationService service = new FilterEvaluationService();
        
        // Тестовий користувач
        UserRequestDto userRU = new UserRequestDto();
        userRU.setClientVersion("2.2.2");
        userRU.setCountry("RU");
        userRU.setSmid("123");
        
        UserRequestDto userDE = new UserRequestDto();
        userDE.setClientVersion("2.2.2");
        userDE.setCountry("DE");
        userDE.setSmid("123");
        
        UserRequestDto userUA = new UserRequestDto();
        userUA.setClientVersion("2.2.2");
        userUA.setCountry("UA");
        userUA.setSmid("123");
        
        // ПРАВИЛЬНИЙ JSON
        MarketingTargetFilterDto correctFilter = createCorrectFilter();
        boolean correctRU = service.evaluateFilter(correctFilter, userRU);
        boolean correctDE = service.evaluateFilter(correctFilter, userDE);
        boolean correctUA = service.evaluateFilter(correctFilter, userUA);
        
        // НЕПРАВИЛЬНИЙ JSON
        MarketingTargetFilterDto incorrectFilter = createIncorrectFilter();
        boolean incorrectRU = service.evaluateFilter(incorrectFilter, userRU);
        boolean incorrectDE = service.evaluateFilter(incorrectFilter, userDE);
        boolean incorrectUA = service.evaluateFilter(incorrectFilter, userUA);
        
        // Виводимо результати
        System.out.println("\n=== ПОРІВНЯННЯ РЕЗУЛЬТАТІВ ===");
        System.out.println("ПРАВИЛЬНИЙ JSON:");
        System.out.println("  RU user: " + correctRU);
        System.out.println("  DE user: " + correctDE);
        System.out.println("  UA user: " + correctUA);
        
        System.out.println("НЕПРАВИЛЬНИЙ JSON:");
        System.out.println("  RU user: " + incorrectRU);
        System.out.println("  DE user: " + incorrectDE);
        System.out.println("  UA user: " + incorrectUA);
        
        System.out.println("\nЧИ ПРАЦЮЮТЬ ОДНАКОВО?");
        boolean sameResults = (correctRU == incorrectRU) && 
                             (correctDE == incorrectDE) && 
                             (correctUA == incorrectUA);
        
        if (sameResults) {
            System.out.println("✅ ТАК! Обидва JSON працюють однаково");
        } else {
            System.out.println("❌ НІ! JSON працюють по-різному");
        }
        
        // Показуємо різниці
        if (correctRU != incorrectRU) System.out.println("  Різниця для RU користувача!");
        if (correctDE != incorrectDE) System.out.println("  Різниця для DE користувача!");
        if (correctUA != incorrectUA) System.out.println("  Різниця для UA користувача!");
    }
    
    private MarketingTargetFilterDto createCorrectFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("ПРАВИЛЬНИЙ");
        filter.setIsActive(true);

        // Root умова: CLIENT_VERSION = "2.2.2"
        FilterConditionDto versionCondition = new FilterConditionDto();
        versionCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionCondition.setOperator(FilterOperator.EQUAL);
        versionCondition.setFieldValue("2.2.2");
        versionCondition.setLogicalOperator(null);
        versionCondition.setOrderIndex(0);
        filter.setConditions(Collections.singletonList(versionCondition));

        // Група: (COUNTRY = "RU" OR COUNTRY = "DE")
        FilterGroupDto countryGroup = new FilterGroupDto();
        countryGroup.setGroupName("Країни");
        countryGroup.setLogicalOperator(LogicalOperator.AND);
        countryGroup.setOrderIndex(0);

        // ПРАВИЛЬНО: перша умова з null
        FilterConditionDto ruCondition = new FilterConditionDto();
        ruCondition.setFieldType(FilterFieldType.COUNTRY);
        ruCondition.setOperator(FilterOperator.EQUAL);
        ruCondition.setFieldValue("RU");
        ruCondition.setLogicalOperator(null); // ✅ ПРАВИЛЬНО
        ruCondition.setOrderIndex(0);

        FilterConditionDto deCondition = new FilterConditionDto();
        deCondition.setFieldType(FilterFieldType.COUNTRY);
        deCondition.setOperator(FilterOperator.EQUAL);
        deCondition.setFieldValue("DE");
        deCondition.setLogicalOperator(LogicalOperator.OR);
        deCondition.setOrderIndex(1);

        countryGroup.setConditions(Arrays.asList(ruCondition, deCondition));
        filter.setGroups(Collections.singletonList(countryGroup));
        return filter;
    }
    
    private MarketingTargetFilterDto createIncorrectFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("НЕПРАВИЛЬНИЙ");
        filter.setIsActive(true);

        // Root умова: CLIENT_VERSION = "2.2.2"
        FilterConditionDto versionCondition = new FilterConditionDto();
        versionCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionCondition.setOperator(FilterOperator.EQUAL);
        versionCondition.setFieldValue("2.2.2");
        versionCondition.setLogicalOperator(null);
        versionCondition.setOrderIndex(0);
        filter.setConditions(Collections.singletonList(versionCondition));

        // Група з ПОМИЛКОЮ
        FilterGroupDto countryGroup = new FilterGroupDto();
        countryGroup.setGroupName("Країни");
        countryGroup.setLogicalOperator(LogicalOperator.AND);
        countryGroup.setOrderIndex(0);

        // НЕПРАВИЛЬНО: перша умова з OR замість null
        FilterConditionDto ruCondition = new FilterConditionDto();
        ruCondition.setFieldType(FilterFieldType.COUNTRY);
        ruCondition.setOperator(FilterOperator.EQUAL);
        ruCondition.setFieldValue("RU");
        ruCondition.setLogicalOperator(LogicalOperator.OR); // ❌ ПОМИЛКА
        ruCondition.setOrderIndex(0);

        FilterConditionDto deCondition = new FilterConditionDto();
        deCondition.setFieldType(FilterFieldType.COUNTRY);
        deCondition.setOperator(FilterOperator.EQUAL);
        deCondition.setFieldValue("DE");
        deCondition.setLogicalOperator(null); // Тут теж неправильно
        deCondition.setOrderIndex(1);

        countryGroup.setConditions(Arrays.asList(ruCondition, deCondition));
        filter.setGroups(Collections.singletonList(countryGroup));
        return filter;
    }
}
