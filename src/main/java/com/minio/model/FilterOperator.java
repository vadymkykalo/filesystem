package com.minio.model;

public enum FilterOperator {
    EQUAL("="),
    NOT_EQUAL("≠"),
    CONTAINS("Co"),
    NOT_CONTAINS("!Co"),
    STARTS_WITH("SW"),
    ENDS_WITH("EW"),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    IN("In"),
    NOT_IN("!In"),
    REGEX("Rx"),
    IS_NULL("NULL"),
    IS_NOT_NULL("!NULL");
    
    private final String symbol;
    
    FilterOperator(String symbol) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return symbol;
    }
}
