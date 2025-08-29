package com.minio.executor;

import com.minio.dto.UserRequestDto;
import com.minio.service.FilterEvaluationService;
import com.minio.util.UserRequestHeraclesMapper;
import com.minio.util.Heracles;

/**
 * Executor for filter evaluation
 */
public class EvaluateFilterExecutor extends AWSExecutor<Boolean> {

    private final FilterEvaluationService evaluationService;
    private final Long filterId;
    private final Heracles heraclesData;

    public EvaluateFilterExecutor(FilterEvaluationService evaluationService, Long filterId, Heracles heraclesData) {
        this.evaluationService = evaluationService;
        this.filterId = filterId;
        this.heraclesData = heraclesData;
    }

    @Override
    public Boolean execute() throws Exception {
        // Unpack UserRequestDto from Heracles
        UserRequestDto userRequest = UserRequestHeraclesMapper.fromHeracles(heraclesData);
        return evaluationService.evaluateFilter(filterId, userRequest);
    }
}
