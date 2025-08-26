package com.minio.repository;

import com.minio.model.MarketingTargetFilterCondition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MarketingTargetFilterConditionRepository {
    
    @PersistenceContext
    EntityManager entityManager;
    
    @Transactional
    public MarketingTargetFilterCondition save(MarketingTargetFilterCondition condition) {
        if (condition.getId() == null) {
            entityManager.persist(condition);
            return condition;
        } else {
            return entityManager.merge(condition);
        }
    }
    
    @Transactional
    public void deleteByFilterId(Long filterId) {
        entityManager.createNamedQuery(
            "MarketingTargetFilterCondition.deleteByFilterId"
        ).setParameter("filterId", filterId).executeUpdate();
    }
}
