package com.minio.executor;

import com.minio.dto.MarketingTargetFilterDto;
import com.minio.service.MarketingTargetFilterService;
import java.util.Optional;

/**
 * Экзекютор для получения фильтра по ID
 */
public class GetFilterByIdExecutor extends AWSExecutor<Optional<MarketingTargetFilterDto>> {

    private final MarketingTargetFilterService filterService;
    private final Long id;

    public GetFilterByIdExecutor(MarketingTargetFilterService filterService, Long id) {
        this.filterService = filterService;
        this.id = id;
    }

    @Override
    public Optional<MarketingTargetFilterDto> execute() throws Exception {
        return filterService.getFilterById(id);
    }
}
