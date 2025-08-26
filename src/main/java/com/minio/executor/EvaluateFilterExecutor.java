package com.minio.executor;

import com.minio.dto.UserRequestDto;
import com.minio.service.FilterEvaluationService;

/**
 * Экзекютор для оценки фильтра
 */
public class EvaluateFilterExecutor extends AWSExecutor<Boolean> {

    private final FilterEvaluationService evaluationService;
    private final Long filterId;
    private final UserRequestDto userRequest;

    public EvaluateFilterExecutor(FilterEvaluationService evaluationService, Long filterId, UserRequestDto userRequest) {
        this.evaluationService = evaluationService;
        this.filterId = filterId;
        this.userRequest = userRequest;
    }

    @Override
    public Boolean execute() throws Exception {
        return evaluationService.evaluateFilter(filterId, userRequest);
    }
}
