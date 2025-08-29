package com.minio.service;

import com.minio.dto.MarketingTargetFilterDto;
import com.minio.dto.FilterConditionDto;
import com.minio.dto.FilterGroupDto;
import com.minio.dto.UserRequestDto;
import com.minio.model.FilterFieldType;
import com.minio.model.FilterOperator;
import com.minio.model.LogicalOperator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Тест для перевірки конкретного JSON фільтра користувача
 * Перевіряє логіку: CLIENT_VERSION = "2.2.2" AND (COUNTRY = "RU" OR COUNTRY = "DE")
 */
public class UserJsonFilterTest {

    private FilterEvaluationService filterEvaluationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        filterEvaluationService = new FilterEvaluationService();
    }

    /**
     * Тест з ПРАВИЛЬНИМ JSON (logicalOperator = null для першої умови в групі)
     * JSON: CLIENT_VERSION = "2.2.2" AND (COUNTRY = "RU" OR COUNTRY = "DE")
     */
    @Test
    public void testCorrectJsonFilter_ShouldWork() {
        System.out.println("\n=== ТЕСТУЄМО ПРАВИЛЬНИЙ JSON ===");
        
        // Створюємо правильний фільтр
        MarketingTargetFilterDto filter = createCorrectFilter();

        // Тестові користувачі
        UserRequestDto userRU_222 = createUser("2.2.2", "RU");
        UserRequestDto userDE_222 = createUser("2.2.2", "DE");
        UserRequestDto userUA_222 = createUser("2.2.2", "UA");
        UserRequestDto userRU_210 = createUser("2.1.0", "RU");

        System.out.println("Фільтр: " + filter.getFilterName());
        
        // Перевірки з логуванням
        boolean resultRU_222 = filterEvaluationService.evaluateFilter(filter, userRU_222);
        boolean resultDE_222 = filterEvaluationService.evaluateFilter(filter, userDE_222);
        boolean resultUA_222 = filterEvaluationService.evaluateFilter(filter, userUA_222);
        boolean resultRU_210 = filterEvaluationService.evaluateFilter(filter, userRU_210);
        
        System.out.println("RU user (2.2.2): " + resultRU_222 + " (очікуємо: true)");
        System.out.println("DE user (2.2.2): " + resultDE_222 + " (очікуємо: true)");
        System.out.println("UA user (2.2.2): " + resultUA_222 + " (очікуємо: false)");
        System.out.println("RU user (2.1.0): " + resultRU_210 + " (очікуємо: false)");
        
        assertTrue(resultRU_222); // ✅ RU + 2.2.2
        assertTrue(resultDE_222); // ✅ DE + 2.2.2
        assertFalse(resultUA_222); // ❌ UA не в фільтрі
        assertFalse(resultRU_210); // ❌ неправильна версія
    }

    /**
     * Тест з НЕПРАВИЛЬНИМ JSON (logicalOperator = "OR" для першої умови в групі)
     * Цей тест показує що станеться з неправильним JSON
     */
    @Test
    public void testIncorrectJsonFilter_MightNotWorkAsExpected() {
        System.out.println("\n=== ТЕСТУЄМО НЕПРАВИЛЬНИЙ JSON ===");
        
        // Створюємо неправильний фільтр (як у користувача)
        MarketingTargetFilterDto filter = createIncorrectFilter();

        // Тестові користувачі
        UserRequestDto userRU_222 = createUser("2.2.2", "RU");
        UserRequestDto userDE_222 = createUser("2.2.2", "DE");
        UserRequestDto userUA_222 = createUser("2.2.2", "UA");

        System.out.println("Фільтр: " + filter.getFilterName());
        
        boolean resultRU = filterEvaluationService.evaluateFilter(filter, userRU_222);
        boolean resultDE = filterEvaluationService.evaluateFilter(filter, userDE_222);
        boolean resultUA = filterEvaluationService.evaluateFilter(filter, userUA_222);
        
        System.out.println("RU user (2.2.2): " + resultRU + " (очікуємо: true)");
        System.out.println("DE user (2.2.2): " + resultDE + " (очікуємо: true)");
        System.out.println("UA user (2.2.2): " + resultUA + " (очікуємо: false)");
        
        // Перевіряємо чи працює як очікується
        if (resultRU && resultDE && !resultUA) {
            System.out.println("✅ НЕПРАВИЛЬНИЙ JSON працює як правильний!");
        } else {
            System.out.println("❌ НЕПРАВИЛЬНИЙ JSON працює інакше!");
        }
    }

    /**
     * Створює ПРАВИЛЬНИЙ фільтр з виправленим JSON
     */
    private MarketingTargetFilterDto createCorrectFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("ПРАВИЛЬНИЙ: Версія 2.2.2 і RU/DE");
        filter.setIsActive(true);

        // Root умова: CLIENT_VERSION = "2.2.2"
        FilterConditionDto versionCondition = new FilterConditionDto();
        versionCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionCondition.setOperator(FilterOperator.EQUAL);
        versionCondition.setFieldValue("2.2.2");
        versionCondition.setLogicalOperator(null); // Перша умова завжди null
        versionCondition.setOrderIndex(0);

        filter.setConditions(Collections.singletonList(versionCondition));

        // Група: (COUNTRY = "RU" OR COUNTRY = "DE")
        FilterGroupDto countryGroup = new FilterGroupDto();
        countryGroup.setGroupName("Країни RU або DE");
        countryGroup.setLogicalOperator(LogicalOperator.AND); // Група з root умовами
        countryGroup.setOrderIndex(0);

        // Умова 1: COUNTRY = "RU" (перша в групі - logicalOperator = null)
        FilterConditionDto ruCondition = new FilterConditionDto();
        ruCondition.setFieldType(FilterFieldType.COUNTRY);
        ruCondition.setOperator(FilterOperator.EQUAL);
        ruCondition.setFieldValue("RU");
        ruCondition.setLogicalOperator(null); // ✅ ПРАВИЛЬНО: null для першої умови
        ruCondition.setOrderIndex(0);

        // Умова 2: COUNTRY = "DE" (друга в групі - logicalOperator = OR)
        FilterConditionDto deCondition = new FilterConditionDto();
        deCondition.setFieldType(FilterFieldType.COUNTRY);
        deCondition.setOperator(FilterOperator.EQUAL);
        deCondition.setFieldValue("DE");
        deCondition.setLogicalOperator(LogicalOperator.OR); // OR з попередньою умовою
        deCondition.setOrderIndex(1);

        countryGroup.setConditions(Arrays.asList(ruCondition, deCondition));
        filter.setGroups(Collections.singletonList(countryGroup));

        return filter;
    }

    /**
     * Створює НЕПРАВИЛЬНИЙ фільтр (як у користувача з помилкою)
     */
    private MarketingTargetFilterDto createIncorrectFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("НЕПРАВИЛЬНИЙ: Версія 2.2.2 і RU/DE");
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
        countryGroup.setGroupName("Країни RU або DE");
        countryGroup.setLogicalOperator(LogicalOperator.AND);
        countryGroup.setOrderIndex(0);

        // Умова 1: COUNTRY = "RU" з ПОМИЛКОЮ
        FilterConditionDto ruCondition = new FilterConditionDto();
        ruCondition.setFieldType(FilterFieldType.COUNTRY);
        ruCondition.setOperator(FilterOperator.EQUAL);
        ruCondition.setFieldValue("RU");
        ruCondition.setLogicalOperator(LogicalOperator.OR); // ❌ ПОМИЛКА: повинно бути null!
        ruCondition.setOrderIndex(0);

        // Умова 2: COUNTRY = "DE"
        FilterConditionDto deCondition = new FilterConditionDto();
        deCondition.setFieldType(FilterFieldType.COUNTRY);
        deCondition.setOperator(FilterOperator.EQUAL);
        deCondition.setFieldValue("DE");
        deCondition.setLogicalOperator(null); // Тут теж помилка в логіці
        deCondition.setOrderIndex(1);

        countryGroup.setConditions(Arrays.asList(ruCondition, deCondition));
        filter.setGroups(Collections.singletonList(countryGroup));

        return filter;
    }

    /**
     * Створює тестового користувача
     */
    private UserRequestDto createUser(String version, String country) {
        UserRequestDto user = new UserRequestDto();
        user.setClientVersion(version);
        user.setCountry(country);
        user.setSmid("123456789");
        user.setOperatingSystem("Android 12");
        user.setBrowser("Chrome");
        return user;
    }
}
