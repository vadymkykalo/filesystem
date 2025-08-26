package com.minio.executor;

import com.minio.dto.MarketingTargetFilterDto;
import com.minio.service.MarketingTargetFilterService;
import java.util.List;

/**
 * Экзекютор для получения всех фильтров
 */
public class GetAllFiltersExecutor extends AWSExecutor<List<MarketingTargetFilterDto>> {

    private final MarketingTargetFilterService filterService;

    public GetAllFiltersExecutor(MarketingTargetFilterService filterService) {
        this.filterService = filterService;
    }

    @Override
    public List<MarketingTargetFilterDto> execute() throws Exception {
        return filterService.getAllFilters();
    }
}
