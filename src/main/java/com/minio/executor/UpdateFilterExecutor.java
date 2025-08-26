package com.minio.executor;

import com.minio.dto.MarketingTargetFilterDto;
import com.minio.service.MarketingTargetFilterService;

/**
 * Экзекютор для обновления фильтра
 */
public class UpdateFilterExecutor extends AWSExecutor<MarketingTargetFilterDto> {

    private final MarketingTargetFilterService filterService;
    private final Long id;
    private final MarketingTargetFilterDto filterDto;

    public UpdateFilterExecutor(MarketingTargetFilterService filterService, Long id, MarketingTargetFilterDto filterDto) {
        this.filterService = filterService;
        this.id = id;
        this.filterDto = filterDto;
    }

    @Override
    public MarketingTargetFilterDto execute() throws Exception {
        return filterService.updateFilter(id, filterDto);
    }
}
