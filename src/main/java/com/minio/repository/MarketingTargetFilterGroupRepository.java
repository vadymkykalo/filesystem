package com.minio.repository;

import com.minio.model.MarketingTargetFilterGroup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MarketingTargetFilterGroupRepository {
    
    @PersistenceContext
    EntityManager entityManager;
    
    @Transactional
    public MarketingTargetFilterGroup save(MarketingTargetFilterGroup group) {
        if (group.getId() == null) {
            entityManager.persist(group);
            return group;
        } else {
            return entityManager.merge(group);
        }
    }
    
    @Transactional
    public void deleteByFilterId(Long filterId) {
        entityManager.createNamedQuery(
            "MarketingTargetFilterGroup.deleteByFilterId"
        ).setParameter("filterId", filterId).executeUpdate();
    }
}
