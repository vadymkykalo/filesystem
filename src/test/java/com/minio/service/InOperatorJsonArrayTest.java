package com.minio.service;

import com.minio.dto.UserRequestDto;
import com.minio.dto.MarketingTargetFilterDto;
import com.minio.dto.FilterConditionDto;
import com.minio.model.FilterFieldType;
import com.minio.model.FilterOperator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test for IN operator with JSON array support
 */
public class InOperatorJsonArrayTest {

    private FilterEvaluationService filterEvaluationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        filterEvaluationService = new FilterEvaluationService();
    }

    @Test
    public void testInOperatorWithJsonArray_ShouldReturnTrue() {
        // Создаем фильтр как в примере пользователя
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("VIP користувачі");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        filter.setGroups(Collections.emptyList());

        // Создаем условие с JSON массивом
        FilterConditionDto condition = new FilterConditionDto();
        condition.setFieldType(FilterFieldType.SMID);
        condition.setOperator(FilterOperator.IN);
        condition.setFieldValue("[\"3586067540\",\"9876543210\",\"1111111111\"]");
        condition.setLogicalOperator(null);
        condition.setOrderIndex(0);

        filter.setConditions(Arrays.asList(condition));

        // Создаем пользователя с SMID из списка
        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setSmid("3586067540");

        // Тестируем - должно вернуть true
        boolean result = filterEvaluationService.evaluateFilter(filter, userRequest);
        assertTrue("Filter should match user with SMID in JSON array", result);
    }

    @Test
    public void testInOperatorWithJsonArray_ShouldReturnFalse() {
        // Создаем тот же фильтр
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("VIP користувачі");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        filter.setGroups(Collections.emptyList());

        FilterConditionDto condition = new FilterConditionDto();
        condition.setFieldType(FilterFieldType.SMID);
        condition.setOperator(FilterOperator.IN);
        condition.setFieldValue("[\"3586067540\",\"9876543210\",\"1111111111\"]");
        condition.setLogicalOperator(null);
        condition.setOrderIndex(0);

        filter.setConditions(Arrays.asList(condition));

        // Создаем пользователя с SMID НЕ из списка
        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setSmid("9999999999");

        // Тестируем - должно вернуть false
        boolean result = filterEvaluationService.evaluateFilter(filter, userRequest);
        assertFalse("Filter should NOT match user with SMID not in JSON array", result);
    }

    @Test
    public void testNotInOperatorWithJsonArray_ShouldReturnTrue() {
        // Тестируем NOT_IN оператор
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Не VIP користувачі");
        filter.setMarketingTargetId(2L);
        filter.setIsActive(true);
        filter.setGroups(Collections.emptyList());

        FilterConditionDto condition = new FilterConditionDto();
        condition.setFieldType(FilterFieldType.SMID);
        condition.setOperator(FilterOperator.NOT_IN);
        condition.setFieldValue("[\"3586067540\",\"9876543210\",\"1111111111\"]");
        condition.setLogicalOperator(null);
        condition.setOrderIndex(0);

        filter.setConditions(Arrays.asList(condition));

        // Пользователь НЕ в списке VIP
        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setSmid("9999999999");

        // Должно вернуть true (пользователь НЕ в VIP списке)
        boolean result = filterEvaluationService.evaluateFilter(filter, userRequest);
        assertTrue("Filter should match user with SMID NOT in JSON array", result);
    }

    @Test
    public void testInOperatorWithRegularCommaSeparated_ShouldWork() {
        // Проверяем что обычные значения через запятую все еще работают
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Regular comma test");
        filter.setMarketingTargetId(3L);
        filter.setIsActive(true);
        filter.setGroups(Collections.emptyList());

        FilterConditionDto condition = new FilterConditionDto();
        condition.setFieldType(FilterFieldType.SMID);
        condition.setOperator(FilterOperator.IN);
        condition.setFieldValue("3586067540,9876543210,1111111111"); // Без JSON скобок
        condition.setLogicalOperator(null);
        condition.setOrderIndex(0);

        filter.setConditions(Arrays.asList(condition));

        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setSmid("9876543210");

        boolean result = filterEvaluationService.evaluateFilter(filter, userRequest);
        assertTrue("Filter should work with regular comma-separated values", result);
    }
}
