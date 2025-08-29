package com.minio.repository;

import com.minio.model.MarketingTarget;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MarketingTargetRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<MarketingTarget> findAll() {
        return entityManager.createNamedQuery("MarketingTarget.findAll", MarketingTarget.class)
                .getResultList();
    }

    public Optional<MarketingTarget> findById(Long id) {
        MarketingTarget target = entityManager.find(MarketingTarget.class, id);
        return Optional.ofNullable(target);
    }

    @Transactional
    public MarketingTarget save(MarketingTarget target) {
        if (target.getId() == null) {
            entityManager.persist(target);
            return target;
        } else {
            return entityManager.merge(target);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        MarketingTarget target = entityManager.find(MarketingTarget.class, id);
        if (target != null) {
            entityManager.remove(target);
        }
    }

    public boolean existsById(Long id) {
        Long count = entityManager.createNamedQuery("MarketingTarget.countById", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }
}
