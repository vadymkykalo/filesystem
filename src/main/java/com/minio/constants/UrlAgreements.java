package com.minio.constants;

/**
 * Константы URL путей для системы таргетинга и маркетинговых инструментов
 */
public final class UrlAgreements {
    
    // Базовые пути для сессий и файлов
    public static final String PATH_SESSION_STORE_CURRENT_LOCALE = "/storeCurrenLocale";
    public static final String PATH_RAW_FILE = "/rawFile";
    
    // Пути для SME онбординга
    public static final String PATH_SME_ONBOARDING = "/sme-onboarding";
    public static final String PATH_SME_ONBOARDING_GENERATE_URL = "/url";
    public static final String PATH_SME_ONBOARDING_SAVE_TARIFF_PLANS = "/tariffPlans/save";
    public static final String PATH_SME_ONBOARDING_SEND_ATTACH_TO_FILE_SERVICE = "/attach/sendToFileService";
    public static final String PATH_SME_ONBOARDING_REFERRAL_LINK = "/sme/referralLink";
    public static final String PATH_SME_ONBOARDING_DECODE_EMPLOYEE_ID = "/sme/decodeEmployeeId";
    
    // Пути для инструментов продаж
    public static final String PATH_SALES_TOOLS = "/salesTools";
    public static final String PATH_SALES_TOOLS_TARGET = "/target";
    public static final String PATH_SALES_TOOLS_NOTIFICATION = "/notification";
    public static final String PATH_SALES_TOOLS_NOTIFICATION_STATUS = "/notification/status";
    public static final String PATH_SALES_TOOLS_MESSAGE = "/message";
    public static final String PATH_SALES_TOOLS_NANO_LANDING = "/nanoLanding";
    public static final String PATH_SALES_TOOLS_NANO_LANDING_STATUS = "/nanoLanding/status";
    
    // Пути для фильтров таргетинга
    public static final String PATH_SALES_TOOLS_FILTERS = "/filters";
    public static final String PATH_SALES_TOOLS_FILTERS_EVALUATE = "/filters/evaluate";
    public static final String PATH_SALES_TOOLS_FILTERS_VALIDATE = "/filters/validate";
    public static final String PATH_SALES_TOOLS_FILTERS_FIELD_TYPES = "/filters/field-types";
    public static final String PATH_SALES_TOOLS_FILTERS_OPERATORS = "/filters/operators";
    public static final String PATH_SALES_TOOLS_FILTERS_LOGICAL_OPERATORS = "/filters/logical-operators";
    
    // Константы для операций
    public static final String OPERATION_CHANGE_STATUS = "/changeStatus";
    public static final String OPERATION_LIST = "/list";
    public static final String OPERATION_FILE = "/file";
    public static final String OPERATION_ID_PARAM = "/{id}";
    
    private UrlAgreements() {
        // Приватный конструктор для предотвращения создания экземпляров
    }
}
