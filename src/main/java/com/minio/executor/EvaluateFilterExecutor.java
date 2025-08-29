package com.minio.executor;

import com.minio.dto.UserRequestDto;
import com.minio.service.FilterEvaluationService;
import com.minio.util.UserRequestHeraclesMapper;
import java.util.Map;

/**
 * Executor for filter evaluation
 */
public class EvaluateFilterExecutor extends AWSExecutor<Boolean> {

    private final FilterEvaluationService evaluationService;
    private final Long filterId;
    private final Map<String, String> heraclesData;

    public EvaluateFilterExecutor(FilterEvaluationService evaluationService, Long filterId, Map<String, String> heraclesData) {
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
