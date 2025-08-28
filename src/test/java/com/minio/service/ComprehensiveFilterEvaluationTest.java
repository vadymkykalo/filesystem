package com.minio.service;

import com.minio.dto.UserRequestDto;
import com.minio.dto.MarketingTargetFilterDto;
import com.minio.dto.FilterConditionDto;
import com.minio.dto.FilterGroupDto;
import com.minio.model.FilterFieldType;
import com.minio.model.FilterOperator;
import com.minio.model.LogicalOperator;
import com.minio.repository.MarketingTargetListItemRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Comprehensive test class for FilterEvaluationService
 * Shows real JSON structures from API and tests them manually without JSON serialization
 * Includes many positive and negative test cases
 */
public class ComprehensiveFilterEvaluationTest {

    @Mock
    private MarketingTargetListItemRepository listItemRepository;

    @Mock
    private MarketingTargetFilterService filterService;

    @InjectMocks
    private FilterEvaluationService filterEvaluationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(listItemRepository.existsBySmidAndFileId(anyString(), anyString())).thenReturn(false);
    }

    @Test
    public void testSimpleUkraineFilter() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Only Ukraine Users",
         *   "marketingTargetId": 1,
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "UA", "orderIndex": 0}
         *   ],
         *   "groups": []
         * }
         */
        
        MarketingTargetFilterDto filter = createSimpleUkraineFilter();
        
        // POSITIVE TESTS
        UserRequestDto ukrainianUser = createUser("12345", "UA", "2.2.2", "Android", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, ukrainianUser));
        
        // NEGATIVE TESTS
        UserRequestDto russianUser = createUser("67890", "RU", "2.2.2", "iOS", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, russianUser));
        
        UserRequestDto germanUser = createUser("11111", "DE", "2.2.1", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, germanUser));
    }

    @Test
    public void testVersionFilter() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Version 2.2.2 Users",
         *   "marketingTargetId": 1,
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "CLIENT_VERSION", "operator": "EQUAL", "fieldValue": "2.2.2", "orderIndex": 0}
         *   ]
         * }
         */
        
        MarketingTargetFilterDto filter = createVersionFilter();
        
        // POSITIVE TESTS
        UserRequestDto user222UA = createUser("12345", "UA", "2.2.2", "Android", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, user222UA));
        
        UserRequestDto user222RU = createUser("67890", "RU", "2.2.2", "iOS", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, user222RU));
        
        // NEGATIVE TESTS
        UserRequestDto user221 = createUser("11111", "UA", "2.2.1", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, user221));
        
        UserRequestDto user223 = createUser("22222", "UA", "2.2.3", "iOS", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, user223));
    }

    @Test
    public void testMultipleAndConditions() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Простой фильтр условий",
         *   "marketingTargetId": 1,
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "3586067540", "orderIndex": 0},
         *     {"fieldType": "COUNTRY", "operator": "NOT_EQUAL", "fieldValue": "BY", "orderIndex": 1},
         *     {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "IOS", "orderIndex": 2}
         *   ]
         * }
         */
        
        MarketingTargetFilterDto filter = createMultipleAndConditionsFilter();
        
        // POSITIVE TESTS
        UserRequestDto matchingUser = createUser("3586067540", "UA", "2.2.2", "iOS 15.0", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, matchingUser));
        
        // NEGATIVE TESTS
        UserRequestDto wrongSmidUser = createUser("1111111111", "UA", "2.2.2", "iOS 15.0", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, wrongSmidUser));
        
        UserRequestDto belarusUser = createUser("3586067540", "BY", "2.2.2", "iOS 15.0", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, belarusUser));
        
        UserRequestDto androidUser = createUser("3586067540", "UA", "2.2.2", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, androidUser));
    }

    @Test
    public void testUkraineOrRussiaWithVersion() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Ukraine or Russia with version 2.2.2",
         *   "marketingTargetId": 1,
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "CLIENT_VERSION", "operator": "EQUAL", "fieldValue": "2.2.2", "orderIndex": 0}
         *   ],
         *   "groups": [
         *     {
         *       "groupName": "Countries",
         *       "logicalOperator": "AND",
         *       "orderIndex": 0,
         *       "conditions": [
         *         {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "UA", "orderIndex": 0},
         *         {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "RU", "orderIndex": 1}
         *       ]
         *     }
         *   ]
         * }
         */
        
        MarketingTargetFilterDto filter = createUkraineOrRussiaWithVersionFilter();
        
        // POSITIVE TESTS
        UserRequestDto ukrainianUser = createUser("12345", "UA", "2.2.2", "Android", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, ukrainianUser));
        
        UserRequestDto russianUser = createUser("67890", "RU", "2.2.2", "iOS", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, russianUser));
        
        // NEGATIVE TESTS
        UserRequestDto ukrainianUserWrongVersion = createUser("11111", "UA", "2.2.1", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, ukrainianUserWrongVersion));
        
        UserRequestDto germanUser = createUser("33333", "DE", "2.2.2", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, germanUser));
    }

    @Test
    public void testMultiGroupFilter() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Мульти-группа фильтр",
         *   "marketingTargetId": 1,
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "UA", "orderIndex": 0}
         *   ],
         *   "groups": [
         *     {
         *       "groupName": "Операционные системы",
         *       "logicalOperator": "AND",
         *       "orderIndex": 0,
         *       "conditions": [
         *         {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "Android", "orderIndex": 0},
         *         {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "iOS", "orderIndex": 1}
         *       ]
         *     },
         *     {
         *       "groupName": "Браузеры",
         *       "logicalOperator": "AND",
         *       "orderIndex": 1,
         *       "conditions": [
         *         {"fieldType": "BROWSER", "operator": "CONTAINS", "fieldValue": "Chrome", "orderIndex": 0},
         *         {"fieldType": "BROWSER", "operator": "CONTAINS", "fieldValue": "Safari", "orderIndex": 1}
         *       ]
         *     }
         *   ]
         * }
         */
        
        MarketingTargetFilterDto filter = createMultiGroupFilter();
        
        // POSITIVE TESTS
        UserRequestDto androidChromeUA = createUser("12345", "UA", "2.2.2", "Android 12", "Google Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, androidChromeUA));
        
        UserRequestDto iosSafariUA = createUser("67890", "UA", "2.2.1", "iOS 16.0", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, iosSafariUA));
        
        // NEGATIVE TESTS
        UserRequestDto androidChromeDE = createUser("33333", "DE", "2.2.2", "Android 12", "Google Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, androidChromeDE));
        
        UserRequestDto androidFirefoxUA = createUser("44444", "UA", "2.2.2", "Android 12", "Firefox");
        assertFalse(filterEvaluationService.evaluateFilter(filter, androidFirefoxUA));
        
        UserRequestDto windowsChromeUA = createUser("66666", "UA", "2.2.2", "Windows 11", "Google Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, windowsChromeUA));
    }

    @Test
    public void testVersionComparisonFilter() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Обновленный фильтр",
         *   "marketingTargetId": 1,
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "CLIENT_VERSION", "operator": "GREATER_THAN", "fieldValue": "2.0.0", "orderIndex": 0}
         *   ]
         * }
         */
        
        MarketingTargetFilterDto filter = createVersionComparisonFilter();
        
        // POSITIVE TESTS
        UserRequestDto user221 = createUser("12345", "UA", "2.2.1", "Android", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, user221));
        
        UserRequestDto user300 = createUser("22222", "BY", "3.0.0", "iOS", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, user300));
        
        // NEGATIVE TESTS
        UserRequestDto user200 = createUser("33333", "UA", "2.0.0", "iOS", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, user200));
        
        UserRequestDto user199 = createUser("44444", "RU", "1.9.9", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, user199));
    }

    @Test
    public void testInactiveFilter() {
        MarketingTargetFilterDto filter = createInactiveFilter();
        
        UserRequestDto ukrainianUser = createUser("12345", "UA", "2.2.2", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, ukrainianUser));
        
        UserRequestDto russianUser = createUser("67890", "RU", "2.2.1", "iOS", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, russianUser));
    }

    @Test
    public void testNullFilter() {
        UserRequestDto user = createUser("12345", "UA", "2.2.2", "Android", "Chrome");
        MarketingTargetFilterDto nullFilter = null;
        assertFalse(filterEvaluationService.evaluateFilter(nullFilter, user));
    }

    @Test
    public void testComplexSmidFilter() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Complex SMID Filter",
         *   "marketingTargetId": 1,
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "CLIENT_VERSION", "operator": "GREATER_THAN_OR_EQUAL", "fieldValue": "2.2.0", "orderIndex": 0},
         *     {"fieldType": "COUNTRY", "operator": "NOT_EQUAL", "fieldValue": "BY", "orderIndex": 1}
         *   ],
         *   "groups": [
         *     {
         *       "groupName": "Target SMID List",
         *       "logicalOperator": "AND",
         *       "orderIndex": 0,
         *       "conditions": [
         *         {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "3586067540", "orderIndex": 0},
         *         {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "9876543210", "orderIndex": 1},
         *         {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "1234567890", "orderIndex": 2}
         *       ]
         *     }
         *   ]
         * }
         */
        
        MarketingTargetFilterDto filter = createComplexSmidFilter();
        
        // POSITIVE TESTS
        UserRequestDto user1 = createUser("3586067540", "UA", "2.2.0", "Android", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, user1));
        
        UserRequestDto user2 = createUser("9876543210", "RU", "2.2.1", "iOS", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, user2));
        
        UserRequestDto user3 = createUser("1234567890", "DE", "3.0.0", "Android", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, user3));
        
        // NEGATIVE TESTS - Wrong version
        UserRequestDto userWrongVersion = createUser("3586067540", "UA", "2.1.9", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, userWrongVersion));
        
        // NEGATIVE TESTS - Belarus (NOT_EQUAL BY)
        UserRequestDto userBelarus = createUser("9876543210", "BY", "2.2.1", "iOS", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, userBelarus));
        
        // NEGATIVE TESTS - Wrong SMID
        UserRequestDto userWrongSmid = createUser("5555555555", "UA", "2.2.1", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, userWrongSmid));
        
        // NEGATIVE TESTS - Multiple failures
        UserRequestDto userMultipleFail = createUser("7777777777", "BY", "2.1.0", "Windows", "Firefox");
        assertFalse(filterEvaluationService.evaluateFilter(filter, userMultipleFail));
    }

    @Test
    public void testMultiPlatformFilter() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Multi-Platform Filter",
         *   "marketingTargetId": 1,
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "COUNTRY", "operator": "IN", "fieldValue": "UA,RU,DE", "orderIndex": 0}
         *   ],
         *   "groups": [
         *     {
         *       "groupName": "Mobile OS",
         *       "logicalOperator": "AND",
         *       "orderIndex": 0,
         *       "conditions": [
         *         {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "Android", "orderIndex": 0},
         *         {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "iOS", "orderIndex": 1}
         *       ]
         *     },
         *     {
         *       "groupName": "Popular Browsers",
         *       "logicalOperator": "AND",
         *       "orderIndex": 1,
         *       "conditions": [
         *         {"fieldType": "BROWSER", "operator": "CONTAINS", "fieldValue": "Chrome", "orderIndex": 0},
         *         {"fieldType": "BROWSER", "operator": "CONTAINS", "fieldValue": "Safari", "orderIndex": 1},
         *         {"fieldType": "BROWSER", "operator": "CONTAINS", "fieldValue": "Firefox", "orderIndex": 2}
         *       ]
         *     }
         *   ]
         * }
         */
        
        MarketingTargetFilterDto filter = createMultiPlatformFilter();
        
        // POSITIVE TESTS - All combinations
        UserRequestDto androidChromeUA = createUser("12345", "UA", "2.2.2", "Android 12", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, androidChromeUA));
        
        UserRequestDto iosSafariRU = createUser("67890", "RU", "2.2.1", "iOS 16.0", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, iosSafariRU));
        
        UserRequestDto androidFirefoxDE = createUser("11111", "DE", "2.2.3", "Android 11", "Firefox");
        assertTrue(filterEvaluationService.evaluateFilter(filter, androidFirefoxDE));
        
        UserRequestDto iosChromeUA = createUser("22222", "UA", "2.2.0", "iOS 15.0", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, iosChromeUA));
        
        UserRequestDto androidSafariRU = createUser("33333", "RU", "2.1.9", "Android 10", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, androidSafariRU));
        
        // NEGATIVE TESTS - Wrong country
        UserRequestDto androidChromeBY = createUser("44444", "BY", "2.2.2", "Android 12", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, androidChromeBY));
        
        // NEGATIVE TESTS - Wrong OS
        UserRequestDto windowsChromeUA = createUser("55555", "UA", "2.2.2", "Windows 11", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, windowsChromeUA));
        
        // NEGATIVE TESTS - Wrong browser
        UserRequestDto androidEdgeUA = createUser("66666", "UA", "2.2.2", "Android 12", "Edge");
        assertFalse(filterEvaluationService.evaluateFilter(filter, androidEdgeUA));
        
        // NEGATIVE TESTS - Multiple failures
        UserRequestDto windowsEdgeFR = createUser("77777", "FR", "2.2.2", "Windows 10", "Edge");
        assertFalse(filterEvaluationService.evaluateFilter(filter, windowsEdgeFR));
    }

    @Test
    public void testAdvancedVersionRegionFilter() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Advanced Regional Filter",
         *   "marketingTargetId": 1,
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "CLIENT_VERSION", "operator": "GREATER_THAN_OR_EQUAL", "fieldValue": "2.0.0", "orderIndex": 0},
         *     {"fieldType": "CLIENT_VERSION", "operator": "LESS_THAN_OR_EQUAL", "fieldValue": "2.5.0", "orderIndex": 1}
         *   ],
         *   "groups": [
         *     {
         *       "groupName": "European Countries",
         *       "logicalOperator": "AND",
         *       "orderIndex": 0,
         *       "conditions": [
         *         {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "UA", "orderIndex": 0},
         *         {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "DE", "orderIndex": 1},
         *         {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "FR", "orderIndex": 2},
         *         {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "IT", "orderIndex": 3}
         *       ]
         *     },
         *     {
         *       "groupName": "North America",
         *       "logicalOperator": "OR",
         *       "orderIndex": 1,
         *       "conditions": [
         *         {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "US", "orderIndex": 0},
         *         {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "CA", "orderIndex": 1}
         *       ]
         *     }
         *   ]
         * }
         */
        
        MarketingTargetFilterDto filter = createAdvancedVersionRegionFilter();
        
        // POSITIVE TESTS - EU countries
        UserRequestDto userUA = createUser("12345", "UA", "2.2.2", "Android", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, userUA));
        
        UserRequestDto userDE = createUser("67890", "DE", "2.0.0", "iOS", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, userDE));
        
        UserRequestDto userFR = createUser("11111", "FR", "2.5.0", "Android", "Firefox");
        assertTrue(filterEvaluationService.evaluateFilter(filter, userFR));
        
        UserRequestDto userIT = createUser("22222", "IT", "2.3.1", "iOS", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, userIT));
        
        // POSITIVE TESTS - North America
        UserRequestDto userUS = createUser("33333", "US", "2.1.5", "Android", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, userUS));
        
        UserRequestDto userCA = createUser("44444", "CA", "2.4.9", "iOS", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, userCA));
        
        // NEGATIVE TESTS - Version too low
        UserRequestDto userVersionLow = createUser("55555", "UA", "1.9.9", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, userVersionLow));
        
        // NEGATIVE TESTS - Version too high
        UserRequestDto userVersionHigh = createUser("66666", "DE", "2.5.1", "iOS", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, userVersionHigh));
        
        // NEGATIVE TESTS - Wrong country
        UserRequestDto userRU = createUser("77777", "RU", "2.2.2", "Android", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, userRU));
        
        UserRequestDto userBY = createUser("88888", "BY", "2.3.0", "iOS", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, userBY));
        
        // NEGATIVE TESTS - Multiple failures
        UserRequestDto userMultipleFail = createUser("99999", "CN", "3.0.0", "Windows", "Edge");
        assertFalse(filterEvaluationService.evaluateFilter(filter, userMultipleFail));
    }

    @Test
    public void testSuperComplexFilter() {
        /*
         * REAL JSON FROM API:
         * {
         *   "filterName": "Super Complex Filter",
         *   "marketingTargetId": 1,
         *   "description": "Maximum complexity filter with multiple conditions and groups",
         *   "isActive": true,
         *   "conditions": [
         *     {"fieldType": "COUNTRY", "operator": "NOT_EQUAL", "fieldValue": "BY", "orderIndex": 0},
         *     {"fieldType": "CLIENT_VERSION", "operator": "GREATER_THAN", "fieldValue": "2.0.0", "orderIndex": 1},
         *     {"fieldType": "CLIENT_VERSION", "operator": "LESS_THAN", "fieldValue": "3.0.0", "orderIndex": 2}
         *   ],
         *   "groups": [
         *     {
         *       "groupName": "VIP SMID List",
         *       "logicalOperator": "AND",
         *       "orderIndex": 0,
         *       "conditions": [
         *         {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "3586067540", "orderIndex": 0},
         *         {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "9876543210", "orderIndex": 1},
         *         {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "1111111111", "orderIndex": 2},
         *         {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "2222222222", "orderIndex": 3}
         *       ]
         *     },
         *     {
         *       "groupName": "Mobile Platforms",
         *       "logicalOperator": "AND",
         *       "orderIndex": 1,
         *       "conditions": [
         *         {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "Android", "orderIndex": 0},
         *         {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "iOS", "orderIndex": 1}
         *       ]
         *     },
         *     {
         *       "groupName": "Modern Browsers",
         *       "logicalOperator": "AND",
         *       "orderIndex": 2,
         *       "conditions": [
         *         {"fieldType": "BROWSER", "operator": "NOT_EQUAL", "fieldValue": "Internet Explorer", "orderIndex": 0},
         *         {"fieldType": "BROWSER", "operator": "NOT_EQUAL", "fieldValue": "Opera Mini", "orderIndex": 1}
         *       ]
         *     }
         *   ]
         * }
         */
        
        MarketingTargetFilterDto filter = createSuperComplexFilter();
        
        // POSITIVE TESTS - Perfect matches
        UserRequestDto perfectUser1 = createUser("3586067540", "UA", "2.5.0", "Android 12", "Chrome");
        assertTrue(filterEvaluationService.evaluateFilter(filter, perfectUser1));
        
        UserRequestDto perfectUser2 = createUser("9876543210", "RU", "2.2.2", "iOS 16.0", "Safari");
        assertTrue(filterEvaluationService.evaluateFilter(filter, perfectUser2));
        
        UserRequestDto perfectUser3 = createUser("1111111111", "DE", "2.8.9", "Android 11", "Firefox");
        assertTrue(filterEvaluationService.evaluateFilter(filter, perfectUser3));
        
        UserRequestDto perfectUser4 = createUser("2222222222", "US", "2.1.0", "iOS 15.0", "Edge");
        assertTrue(filterEvaluationService.evaluateFilter(filter, perfectUser4));
        
        // NEGATIVE TESTS - Belarus (NOT_EQUAL BY)
        UserRequestDto belarusUser = createUser("3586067540", "BY", "2.5.0", "Android 12", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, belarusUser));
        
        // NEGATIVE TESTS - Wrong version range
        UserRequestDto lowVersionUser = createUser("9876543210", "UA", "2.0.0", "iOS 16.0", "Safari");
        assertFalse(filterEvaluationService.evaluateFilter(filter, lowVersionUser));
        
        UserRequestDto highVersionUser = createUser("1111111111", "RU", "3.0.0", "Android 11", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, highVersionUser));
        
        // NEGATIVE TESTS - Wrong SMID
        UserRequestDto wrongSmidUser = createUser("5555555555", "UA", "2.5.0", "Android 12", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, wrongSmidUser));
        
        // NEGATIVE TESTS - Wrong OS
        UserRequestDto windowsUser = createUser("3586067540", "UA", "2.5.0", "Windows 11", "Chrome");
        assertFalse(filterEvaluationService.evaluateFilter(filter, windowsUser));
        
        // NEGATIVE TESTS - Old browser
        UserRequestDto ieUser = createUser("9876543210", "RU", "2.2.2", "Android 12", "Internet Explorer");
        assertFalse(filterEvaluationService.evaluateFilter(filter, ieUser));
        
        UserRequestDto operaMiniUser = createUser("1111111111", "DE", "2.8.9", "iOS 15.0", "Opera Mini");
        assertFalse(filterEvaluationService.evaluateFilter(filter, operaMiniUser));
        
        // NEGATIVE TESTS - Multiple failures
        UserRequestDto totalFailUser = createUser("7777777777", "BY", "1.5.0", "Windows XP", "Internet Explorer");
        assertFalse(filterEvaluationService.evaluateFilter(filter, totalFailUser));
    }

    // ===== HELPER METHODS FOR CREATING FILTERS =====

    private MarketingTargetFilterDto createSimpleUkraineFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Only Ukraine Users");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        FilterConditionDto condition = new FilterConditionDto();
        condition.setFieldType(FilterFieldType.COUNTRY);
        condition.setOperator(FilterOperator.EQUAL);
        condition.setFieldValue("UA");
        condition.setOrderIndex(0);
        
        filter.setConditions(Arrays.asList(condition));
        filter.setGroups(Collections.emptyList());
        return filter;
    }

    private MarketingTargetFilterDto createVersionFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Version 2.2.2 Users");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        FilterConditionDto condition = new FilterConditionDto();
        condition.setFieldType(FilterFieldType.CLIENT_VERSION);
        condition.setOperator(FilterOperator.EQUAL);
        condition.setFieldValue("2.2.2");
        condition.setOrderIndex(0);
        
        filter.setConditions(Arrays.asList(condition));
        filter.setGroups(Collections.emptyList());
        return filter;
    }

    private MarketingTargetFilterDto createMultipleAndConditionsFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Простой фильтр условий");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        FilterConditionDto smidCondition = new FilterConditionDto();
        smidCondition.setFieldType(FilterFieldType.SMID);
        smidCondition.setOperator(FilterOperator.EQUAL);
        smidCondition.setFieldValue("3586067540");
        smidCondition.setOrderIndex(0);
        smidCondition.setLogicalOperator(null);
        
        FilterConditionDto countryCondition = new FilterConditionDto();
        countryCondition.setFieldType(FilterFieldType.COUNTRY);
        countryCondition.setOperator(FilterOperator.NOT_EQUAL);
        countryCondition.setFieldValue("BY");
        countryCondition.setOrderIndex(1);
        countryCondition.setLogicalOperator(LogicalOperator.AND);
        
        FilterConditionDto osCondition = new FilterConditionDto();
        osCondition.setFieldType(FilterFieldType.OPERATING_SYSTEM);
        osCondition.setOperator(FilterOperator.CONTAINS);
        osCondition.setFieldValue("IOS");
        osCondition.setOrderIndex(2);
        osCondition.setLogicalOperator(LogicalOperator.AND);
        
        filter.setConditions(Arrays.asList(smidCondition, countryCondition, osCondition));
        filter.setGroups(Collections.emptyList());
        return filter;
    }

    private MarketingTargetFilterDto createUkraineOrRussiaWithVersionFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Ukraine or Russia with version 2.2.2");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        FilterConditionDto versionCondition = new FilterConditionDto();
        versionCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionCondition.setOperator(FilterOperator.EQUAL);
        versionCondition.setFieldValue("2.2.2");
        versionCondition.setOrderIndex(0);
        filter.setConditions(Arrays.asList(versionCondition));
        
        FilterGroupDto countryGroup = new FilterGroupDto();
        countryGroup.setGroupName("Countries");
        countryGroup.setLogicalOperator(LogicalOperator.AND);
        countryGroup.setOrderIndex(0);
        
        FilterConditionDto uaCondition = new FilterConditionDto();
        uaCondition.setFieldType(FilterFieldType.COUNTRY);
        uaCondition.setOperator(FilterOperator.EQUAL);
        uaCondition.setFieldValue("UA");
        uaCondition.setOrderIndex(0);
        uaCondition.setLogicalOperator(null);
        
        FilterConditionDto ruCondition = new FilterConditionDto();
        ruCondition.setFieldType(FilterFieldType.COUNTRY);
        ruCondition.setOperator(FilterOperator.EQUAL);
        ruCondition.setFieldValue("RU");
        ruCondition.setOrderIndex(1);
        ruCondition.setLogicalOperator(LogicalOperator.OR);
        
        countryGroup.setConditions(Arrays.asList(uaCondition, ruCondition));
        filter.setGroups(Arrays.asList(countryGroup));
        return filter;
    }

    private MarketingTargetFilterDto createMultiGroupFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Мульти-группа фильтр");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        FilterConditionDto countryCondition = new FilterConditionDto();
        countryCondition.setFieldType(FilterFieldType.COUNTRY);
        countryCondition.setOperator(FilterOperator.EQUAL);
        countryCondition.setFieldValue("UA");
        countryCondition.setOrderIndex(0);
        filter.setConditions(Arrays.asList(countryCondition));
        
        // OS Group
        FilterGroupDto osGroup = new FilterGroupDto();
        osGroup.setGroupName("Операционные системы");
        osGroup.setLogicalOperator(LogicalOperator.AND);
        osGroup.setOrderIndex(0);
        
        FilterConditionDto androidCondition = new FilterConditionDto();
        androidCondition.setFieldType(FilterFieldType.OPERATING_SYSTEM);
        androidCondition.setOperator(FilterOperator.CONTAINS);
        androidCondition.setFieldValue("Android");
        androidCondition.setOrderIndex(0);
        androidCondition.setLogicalOperator(null);
        
        FilterConditionDto iosCondition = new FilterConditionDto();
        iosCondition.setFieldType(FilterFieldType.OPERATING_SYSTEM);
        iosCondition.setOperator(FilterOperator.CONTAINS);
        iosCondition.setFieldValue("iOS");
        iosCondition.setOrderIndex(1);
        iosCondition.setLogicalOperator(LogicalOperator.OR);
        
        osGroup.setConditions(Arrays.asList(androidCondition, iosCondition));
        
        // Browser Group
        FilterGroupDto browserGroup = new FilterGroupDto();
        browserGroup.setGroupName("Браузеры");
        browserGroup.setLogicalOperator(LogicalOperator.AND);
        browserGroup.setOrderIndex(1);
        
        FilterConditionDto chromeCondition = new FilterConditionDto();
        chromeCondition.setFieldType(FilterFieldType.BROWSER);
        chromeCondition.setOperator(FilterOperator.CONTAINS);
        chromeCondition.setFieldValue("Chrome");
        chromeCondition.setOrderIndex(0);
        chromeCondition.setLogicalOperator(null);
        
        FilterConditionDto safariCondition = new FilterConditionDto();
        safariCondition.setFieldType(FilterFieldType.BROWSER);
        safariCondition.setOperator(FilterOperator.CONTAINS);
        safariCondition.setFieldValue("Safari");
        safariCondition.setOrderIndex(1);
        safariCondition.setLogicalOperator(LogicalOperator.OR);
        
        browserGroup.setConditions(Arrays.asList(chromeCondition, safariCondition));
        
        filter.setGroups(Arrays.asList(osGroup, browserGroup));
        return filter;
    }

    private MarketingTargetFilterDto createVersionComparisonFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Обновленный фильтр");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        FilterConditionDto versionCondition = new FilterConditionDto();
        versionCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionCondition.setOperator(FilterOperator.GREATER_THAN);
        versionCondition.setFieldValue("2.0.0");
        versionCondition.setOrderIndex(0);
        
        filter.setConditions(Arrays.asList(versionCondition));
        filter.setGroups(Collections.emptyList());
        return filter;
    }

    private MarketingTargetFilterDto createInactiveFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Inactive Filter");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(false);
        
        FilterConditionDto condition = new FilterConditionDto();
        condition.setFieldType(FilterFieldType.COUNTRY);
        condition.setOperator(FilterOperator.EQUAL);
        condition.setFieldValue("UA");
        condition.setOrderIndex(0);
        
        filter.setConditions(Arrays.asList(condition));
        filter.setGroups(Collections.emptyList());
        return filter;
    }

    private MarketingTargetFilterDto createComplexSmidFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Complex SMID Filter");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        // Root conditions
        FilterConditionDto versionCondition = new FilterConditionDto();
        versionCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionCondition.setOperator(FilterOperator.GREATER_THAN_OR_EQUAL);
        versionCondition.setFieldValue("2.2.0");
        versionCondition.setOrderIndex(0);
        versionCondition.setLogicalOperator(null);
        
        FilterConditionDto countryCondition = new FilterConditionDto();
        countryCondition.setFieldType(FilterFieldType.COUNTRY);
        countryCondition.setOperator(FilterOperator.NOT_EQUAL);
        countryCondition.setFieldValue("BY");
        countryCondition.setOrderIndex(1);
        countryCondition.setLogicalOperator(LogicalOperator.AND);
        
        filter.setConditions(Arrays.asList(versionCondition, countryCondition));
        
        // SMID Group
        FilterGroupDto smidGroup = new FilterGroupDto();
        smidGroup.setGroupName("Target SMID List");
        smidGroup.setLogicalOperator(LogicalOperator.AND);
        smidGroup.setOrderIndex(0);
        
        FilterConditionDto smid1 = new FilterConditionDto();
        smid1.setFieldType(FilterFieldType.SMID);
        smid1.setOperator(FilterOperator.EQUAL);
        smid1.setFieldValue("3586067540");
        smid1.setOrderIndex(0);
        smid1.setLogicalOperator(null);
        
        FilterConditionDto smid2 = new FilterConditionDto();
        smid2.setFieldType(FilterFieldType.SMID);
        smid2.setOperator(FilterOperator.EQUAL);
        smid2.setFieldValue("9876543210");
        smid2.setOrderIndex(1);
        smid2.setLogicalOperator(LogicalOperator.OR);
        
        FilterConditionDto smid3 = new FilterConditionDto();
        smid3.setFieldType(FilterFieldType.SMID);
        smid3.setOperator(FilterOperator.EQUAL);
        smid3.setFieldValue("1234567890");
        smid3.setOrderIndex(2);
        smid3.setLogicalOperator(LogicalOperator.OR);
        
        smidGroup.setConditions(Arrays.asList(smid1, smid2, smid3));
        filter.setGroups(Arrays.asList(smidGroup));
        return filter;
    }

    private MarketingTargetFilterDto createMultiPlatformFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Multi-Platform Filter");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        // Country IN condition
        FilterConditionDto countryCondition = new FilterConditionDto();
        countryCondition.setFieldType(FilterFieldType.COUNTRY);
        countryCondition.setOperator(FilterOperator.IN);
        countryCondition.setFieldValue("UA,RU,DE");
        countryCondition.setOrderIndex(0);
        filter.setConditions(Arrays.asList(countryCondition));
        
        // OS Group
        FilterGroupDto osGroup = new FilterGroupDto();
        osGroup.setGroupName("Mobile OS");
        osGroup.setLogicalOperator(LogicalOperator.AND);
        osGroup.setOrderIndex(0);
        
        FilterConditionDto androidCondition = new FilterConditionDto();
        androidCondition.setFieldType(FilterFieldType.OPERATING_SYSTEM);
        androidCondition.setOperator(FilterOperator.CONTAINS);
        androidCondition.setFieldValue("Android");
        androidCondition.setOrderIndex(0);
        androidCondition.setLogicalOperator(null);
        
        FilterConditionDto iosCondition = new FilterConditionDto();
        iosCondition.setFieldType(FilterFieldType.OPERATING_SYSTEM);
        iosCondition.setOperator(FilterOperator.CONTAINS);
        iosCondition.setFieldValue("iOS");
        iosCondition.setOrderIndex(1);
        iosCondition.setLogicalOperator(LogicalOperator.OR);
        
        osGroup.setConditions(Arrays.asList(androidCondition, iosCondition));
        
        // Browser Group
        FilterGroupDto browserGroup = new FilterGroupDto();
        browserGroup.setGroupName("Popular Browsers");
        browserGroup.setLogicalOperator(LogicalOperator.AND);
        browserGroup.setOrderIndex(1);
        
        FilterConditionDto chromeCondition = new FilterConditionDto();
        chromeCondition.setFieldType(FilterFieldType.BROWSER);
        chromeCondition.setOperator(FilterOperator.CONTAINS);
        chromeCondition.setFieldValue("Chrome");
        chromeCondition.setOrderIndex(0);
        chromeCondition.setLogicalOperator(null);
        
        FilterConditionDto safariCondition = new FilterConditionDto();
        safariCondition.setFieldType(FilterFieldType.BROWSER);
        safariCondition.setOperator(FilterOperator.CONTAINS);
        safariCondition.setFieldValue("Safari");
        safariCondition.setOrderIndex(1);
        safariCondition.setLogicalOperator(LogicalOperator.OR);
        
        FilterConditionDto firefoxCondition = new FilterConditionDto();
        firefoxCondition.setFieldType(FilterFieldType.BROWSER);
        firefoxCondition.setOperator(FilterOperator.CONTAINS);
        firefoxCondition.setFieldValue("Firefox");
        firefoxCondition.setOrderIndex(2);
        firefoxCondition.setLogicalOperator(LogicalOperator.OR);
        
        browserGroup.setConditions(Arrays.asList(chromeCondition, safariCondition, firefoxCondition));
        
        filter.setGroups(Arrays.asList(osGroup, browserGroup));
        return filter;
    }

    private MarketingTargetFilterDto createAdvancedVersionRegionFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Advanced Regional Filter");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        // Version range conditions
        FilterConditionDto versionMinCondition = new FilterConditionDto();
        versionMinCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionMinCondition.setOperator(FilterOperator.GREATER_THAN_OR_EQUAL);
        versionMinCondition.setFieldValue("2.0.0");
        versionMinCondition.setOrderIndex(0);
        versionMinCondition.setLogicalOperator(null);
        
        FilterConditionDto versionMaxCondition = new FilterConditionDto();
        versionMaxCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionMaxCondition.setOperator(FilterOperator.LESS_THAN_OR_EQUAL);
        versionMaxCondition.setFieldValue("2.5.0");
        versionMaxCondition.setOrderIndex(1);
        versionMaxCondition.setLogicalOperator(LogicalOperator.AND);
        
        filter.setConditions(Arrays.asList(versionMinCondition, versionMaxCondition));
        
        // EU Countries Group
        FilterGroupDto euGroup = new FilterGroupDto();
        euGroup.setGroupName("European Countries");
        euGroup.setLogicalOperator(LogicalOperator.AND);
        euGroup.setOrderIndex(0);
        
        FilterConditionDto uaCondition = new FilterConditionDto();
        uaCondition.setFieldType(FilterFieldType.COUNTRY);
        uaCondition.setOperator(FilterOperator.EQUAL);
        uaCondition.setFieldValue("UA");
        uaCondition.setOrderIndex(0);
        uaCondition.setLogicalOperator(null);
        
        FilterConditionDto deCondition = new FilterConditionDto();
        deCondition.setFieldType(FilterFieldType.COUNTRY);
        deCondition.setOperator(FilterOperator.EQUAL);
        deCondition.setFieldValue("DE");
        deCondition.setOrderIndex(1);
        deCondition.setLogicalOperator(LogicalOperator.OR);
        
        FilterConditionDto frCondition = new FilterConditionDto();
        frCondition.setFieldType(FilterFieldType.COUNTRY);
        frCondition.setOperator(FilterOperator.EQUAL);
        frCondition.setFieldValue("FR");
        frCondition.setOrderIndex(2);
        frCondition.setLogicalOperator(LogicalOperator.OR);
        
        FilterConditionDto itCondition = new FilterConditionDto();
        itCondition.setFieldType(FilterFieldType.COUNTRY);
        itCondition.setOperator(FilterOperator.EQUAL);
        itCondition.setFieldValue("IT");
        itCondition.setOrderIndex(3);
        itCondition.setLogicalOperator(LogicalOperator.OR);
        
        euGroup.setConditions(Arrays.asList(uaCondition, deCondition, frCondition, itCondition));
        
        // North America Group
        FilterGroupDto naGroup = new FilterGroupDto();
        naGroup.setGroupName("North America");
        naGroup.setLogicalOperator(LogicalOperator.OR);
        naGroup.setOrderIndex(1);
        
        FilterConditionDto usCondition = new FilterConditionDto();
        usCondition.setFieldType(FilterFieldType.COUNTRY);
        usCondition.setOperator(FilterOperator.EQUAL);
        usCondition.setFieldValue("US");
        usCondition.setOrderIndex(0);
        usCondition.setLogicalOperator(null);
        
        FilterConditionDto caCondition = new FilterConditionDto();
        caCondition.setFieldType(FilterFieldType.COUNTRY);
        caCondition.setOperator(FilterOperator.EQUAL);
        caCondition.setFieldValue("CA");
        caCondition.setOrderIndex(1);
        caCondition.setLogicalOperator(LogicalOperator.OR);
        
        naGroup.setConditions(Arrays.asList(usCondition, caCondition));
        
        filter.setGroups(Arrays.asList(euGroup, naGroup));
        return filter;
    }

    private MarketingTargetFilterDto createSuperComplexFilter() {
        MarketingTargetFilterDto filter = new MarketingTargetFilterDto();
        filter.setFilterName("Super Complex Filter");
        filter.setMarketingTargetId(1L);
        filter.setIsActive(true);
        
        // Root conditions
        FilterConditionDto countryNotByCondition = new FilterConditionDto();
        countryNotByCondition.setFieldType(FilterFieldType.COUNTRY);
        countryNotByCondition.setOperator(FilterOperator.NOT_EQUAL);
        countryNotByCondition.setFieldValue("BY");
        countryNotByCondition.setOrderIndex(0);
        countryNotByCondition.setLogicalOperator(null);
        
        FilterConditionDto versionMinCondition = new FilterConditionDto();
        versionMinCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionMinCondition.setOperator(FilterOperator.GREATER_THAN);
        versionMinCondition.setFieldValue("2.0.0");
        versionMinCondition.setOrderIndex(1);
        versionMinCondition.setLogicalOperator(LogicalOperator.AND);
        
        FilterConditionDto versionMaxCondition = new FilterConditionDto();
        versionMaxCondition.setFieldType(FilterFieldType.CLIENT_VERSION);
        versionMaxCondition.setOperator(FilterOperator.LESS_THAN);
        versionMaxCondition.setFieldValue("3.0.0");
        versionMaxCondition.setOrderIndex(2);
        versionMaxCondition.setLogicalOperator(LogicalOperator.AND);
        
        filter.setConditions(Arrays.asList(countryNotByCondition, versionMinCondition, versionMaxCondition));
        
        // VIP SMID Group
        FilterGroupDto vipSmidGroup = new FilterGroupDto();
        vipSmidGroup.setGroupName("VIP SMID List");
        vipSmidGroup.setLogicalOperator(LogicalOperator.AND);
        vipSmidGroup.setOrderIndex(0);
        
        FilterConditionDto vipSmid1 = new FilterConditionDto();
        vipSmid1.setFieldType(FilterFieldType.SMID);
        vipSmid1.setOperator(FilterOperator.EQUAL);
        vipSmid1.setFieldValue("3586067540");
        vipSmid1.setOrderIndex(0);
        vipSmid1.setLogicalOperator(null);
        
        FilterConditionDto vipSmid2 = new FilterConditionDto();
        vipSmid2.setFieldType(FilterFieldType.SMID);
        vipSmid2.setOperator(FilterOperator.EQUAL);
        vipSmid2.setFieldValue("9876543210");
        vipSmid2.setOrderIndex(1);
        vipSmid2.setLogicalOperator(LogicalOperator.OR);
        
        FilterConditionDto vipSmid3 = new FilterConditionDto();
        vipSmid3.setFieldType(FilterFieldType.SMID);
        vipSmid3.setOperator(FilterOperator.EQUAL);
        vipSmid3.setFieldValue("1111111111");
        vipSmid3.setOrderIndex(2);
        vipSmid3.setLogicalOperator(LogicalOperator.OR);
        
        FilterConditionDto vipSmid4 = new FilterConditionDto();
        vipSmid4.setFieldType(FilterFieldType.SMID);
        vipSmid4.setOperator(FilterOperator.EQUAL);
        vipSmid4.setFieldValue("2222222222");
        vipSmid4.setOrderIndex(3);
        vipSmid4.setLogicalOperator(LogicalOperator.OR);
        
        vipSmidGroup.setConditions(Arrays.asList(vipSmid1, vipSmid2, vipSmid3, vipSmid4));
        
        // Mobile Platforms Group
        FilterGroupDto mobilePlatformsGroup = new FilterGroupDto();
        mobilePlatformsGroup.setGroupName("Mobile Platforms");
        mobilePlatformsGroup.setLogicalOperator(LogicalOperator.AND);
        mobilePlatformsGroup.setOrderIndex(1);
        
        FilterConditionDto androidPlatform = new FilterConditionDto();
        androidPlatform.setFieldType(FilterFieldType.OPERATING_SYSTEM);
        androidPlatform.setOperator(FilterOperator.CONTAINS);
        androidPlatform.setFieldValue("Android");
        androidPlatform.setOrderIndex(0);
        androidPlatform.setLogicalOperator(null);
        
        FilterConditionDto iosPlatform = new FilterConditionDto();
        iosPlatform.setFieldType(FilterFieldType.OPERATING_SYSTEM);
        iosPlatform.setOperator(FilterOperator.CONTAINS);
        iosPlatform.setFieldValue("iOS");
        iosPlatform.setOrderIndex(1);
        iosPlatform.setLogicalOperator(LogicalOperator.OR);
        
        mobilePlatformsGroup.setConditions(Arrays.asList(androidPlatform, iosPlatform));
        
        // Modern Browsers Group
        FilterGroupDto modernBrowsersGroup = new FilterGroupDto();
        modernBrowsersGroup.setGroupName("Modern Browsers");
        modernBrowsersGroup.setLogicalOperator(LogicalOperator.AND);
        modernBrowsersGroup.setOrderIndex(2);
        
        FilterConditionDto notIE = new FilterConditionDto();
        notIE.setFieldType(FilterFieldType.BROWSER);
        notIE.setOperator(FilterOperator.NOT_EQUAL);
        notIE.setFieldValue("Internet Explorer");
        notIE.setOrderIndex(0);
        notIE.setLogicalOperator(null);
        
        FilterConditionDto notOperaMini = new FilterConditionDto();
        notOperaMini.setFieldType(FilterFieldType.BROWSER);
        notOperaMini.setOperator(FilterOperator.NOT_EQUAL);
        notOperaMini.setFieldValue("Opera Mini");
        notOperaMini.setOrderIndex(1);
        notOperaMini.setLogicalOperator(LogicalOperator.AND);
        
        modernBrowsersGroup.setConditions(Arrays.asList(notIE, notOperaMini));
        
        filter.setGroups(Arrays.asList(vipSmidGroup, mobilePlatformsGroup, modernBrowsersGroup));
        return filter;
    }

    private UserRequestDto createUser(String smid, String country, String clientVersion, 
                                    String operatingSystem, String browser) {
        UserRequestDto user = new UserRequestDto();
        user.setSmid(smid);
        user.setCountry(country);
        user.setClientVersion(clientVersion);
        user.setOperatingSystem(operatingSystem);
        user.setBrowser(browser);
        user.setLanguage("en");
        return user;
    }
}
