package com.minio.repository;

import com.minio.model.FileEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FileRepository {
    
    @PersistenceContext
    EntityManager entityManager;
    
    @Transactional
    public FileEntity save(FileEntity fileEntity) {
        entityManager.persist(fileEntity);
        return fileEntity;
    }
    
    public Optional<FileEntity> findById(String id) {
        FileEntity entity = entityManager.find(FileEntity.class, id);
        return Optional.ofNullable(entity);
    }
    
    public List<FileEntity> findAll() {
        TypedQuery<FileEntity> query = entityManager.createQuery(
            "SELECT f FROM FileEntity f ORDER BY f.uploadDate DESC", 
            FileEntity.class
        );
        return query.getResultList();
    }
    
    @Transactional
    public void deleteById(String id) {
        FileEntity entity = entityManager.find(FileEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
    
    public List<FileEntity> findByFileName(String fileName) {
        TypedQuery<FileEntity> query = entityManager.createNamedQuery(
            "FileEntity.findByFileName", FileEntity.class
        );
        query.setParameter("fileName", "%" + fileName + "%");
        return query.getResultList();
    }
    
    public List<FileEntity> findByContentType(String contentType) {
        TypedQuery<FileEntity> query = entityManager.createNamedQuery(
            "FileEntity.findByContentType", FileEntity.class
        );
        query.setParameter("contentType", contentType);
        return query.getResultList();
    }
}
