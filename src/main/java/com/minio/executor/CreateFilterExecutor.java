package com.minio.executor;

import com.minio.dto.MarketingTargetFilterDto;
import com.minio.service.MarketingTargetFilterService;

/**
 * Экзекютор для создания фильтра
 */
public class CreateFilterExecutor extends AWSExecutor<MarketingTargetFilterDto> {

    private final MarketingTargetFilterService filterService;
    private final MarketingTargetFilterDto filterDto;

    public CreateFilterExecutor(MarketingTargetFilterService filterService, MarketingTargetFilterDto filterDto) {
        this.filterService = filterService;
        this.filterDto = filterDto;
    }

    @Override
    public MarketingTargetFilterDto execute() throws Exception {
        return filterService.createFilter(filterDto);
    }
}
