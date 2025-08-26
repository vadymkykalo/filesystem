package com.minio.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class MarketingTargetListItemRepository {
    
    @PersistenceContext
    EntityManager entityManager;
    
    /**
     * Проверить существование элемента списка по ID маркетингового таргета и SMID
     */
    public boolean existsByMarketingTargetIdAndSmid(Long marketingTargetId, String smid) {
        TypedQuery<Long> query = entityManager.createNamedQuery(
            "MarketingTargetListItem.countByMarketingTargetIdAndSmid",
            Long.class
        );
        query.setParameter("marketingTargetId", marketingTargetId);
        query.setParameter("smid", smid);
        return query.getSingleResult() > 0;
    }
    
    /**
     * Проверить существование SMID в файле выборки (для фильтров)
     */
    public boolean existsBySmidAndFileId(String smid, String fileId) {
        // fileId здесь может быть UUID файла или ID маркетингового таргета
        // В зависимости от реализации, можно использовать разные подходы
        try {
            Long marketingTargetId = Long.parseLong(fileId);
            return existsByMarketingTargetIdAndSmid(marketingTargetId, smid);
        } catch (NumberFormatException e) {
            // Если fileId не число, возможно это UUID файла
            // Здесь можно добавить логику поиска по UUID файла
            return false;
        }
    }
}
