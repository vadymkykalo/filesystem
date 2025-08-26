package com.minio.repository;

import com.minio.model.MarketingTargetFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MarketingTargetFilterRepository {
    
    @PersistenceContext
    EntityManager entityManager;
    
    public List<MarketingTargetFilter> findAll() {
        return entityManager.createNamedQuery(
            "MarketingTargetFilter.findAll", 
            MarketingTargetFilter.class
        ).getResultList();
    }
    
    public Optional<MarketingTargetFilter> findById(Long id) {
        MarketingTargetFilter filter = entityManager.find(MarketingTargetFilter.class, id);
        return Optional.ofNullable(filter);
    }
    
    public List<MarketingTargetFilter> findByMarketingTargetId(Long marketingTargetId) {
        TypedQuery<MarketingTargetFilter> query = entityManager.createNamedQuery(
            "MarketingTargetFilter.findByMarketingTargetId",
            MarketingTargetFilter.class
        );
        query.setParameter("marketingTargetId", marketingTargetId);
        return query.getResultList();
    }
    
    public Optional<MarketingTargetFilter> findByIdWithConditions(Long id) {
        TypedQuery<MarketingTargetFilter> query = entityManager.createNamedQuery(
            "MarketingTargetFilter.findByIdWithConditions",
            MarketingTargetFilter.class
        );
        query.setParameter("id", id);
        List<MarketingTargetFilter> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    @Transactional
    public MarketingTargetFilter save(MarketingTargetFilter filter) {
        if (filter.getId() == null) {
            entityManager.persist(filter);
            return filter;
        } else {
            return entityManager.merge(filter);
        }
    }
    
    @Transactional
    public void deleteById(Long id) {
        MarketingTargetFilter filter = entityManager.find(MarketingTargetFilter.class, id);
        if (filter != null) {
            entityManager.remove(filter);
        }
    }
    
    public boolean existsByFilterName(String filterName) {
        TypedQuery<Long> query = entityManager.createNamedQuery(
            "MarketingTargetFilter.existsByFilterName",
            Long.class
        );
        query.setParameter("filterName", filterName);
        return query.getSingleResult() > 0;
    }
    
    public boolean existsByFilterNameAndIdNot(String filterName, Long id) {
        TypedQuery<Long> query = entityManager.createNamedQuery(
            "MarketingTargetFilter.existsByFilterNameAndIdNot",
            Long.class
        );
        query.setParameter("filterName", filterName);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }
}
