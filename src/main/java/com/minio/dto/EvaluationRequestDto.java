package com.minio.dto;

/**
 * DTO for filter evaluation request
 */
public class EvaluationRequestDto {
    
    private MarketingTargetFilterDto filter;
    private UserRequestDto userRequest;
    
    public EvaluationRequestDto() {}
    
    public MarketingTargetFilterDto getFilter() {
        return filter;
    }
    
    public void setFilter(MarketingTargetFilterDto filter) {
        this.filter = filter;
    }
    
    public UserRequestDto getUserRequest() {
        return userRequest;
    }
    
    public void setUserRequest(UserRequestDto userRequest) {
        this.userRequest = userRequest;
    }
}
