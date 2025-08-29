package com.minio.executor;

import com.minio.service.MarketingTargetFilterService;

/**
 * Executor for filter deletion
 */
public class DeleteFilterExecutor extends AWSExecutor<Void> {

    private final MarketingTargetFilterService filterService;
    private final Long id;

    public DeleteFilterExecutor(MarketingTargetFilterService filterService, Long id) {
        this.filterService = filterService;
        this.id = id;
    }

    @Override
    public Void execute() throws Exception {
        filterService.deleteFilter(id);
        return null;
    }
}
