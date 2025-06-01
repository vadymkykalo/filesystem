package com.minio.repository;

import com.minio.model.FileEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

@ApplicationScoped
public class FileRepository {
    
    @Inject
    DSLContext dsl;
    
    private static final String TABLE_NAME = "files";
    
    public FileEntity save(FileEntity fileEntity) {
        dsl.insertInto(table(TABLE_NAME))
            .set(field("id"), fileEntity.getId())
            .set(field("file_name"), fileEntity.getFileName())
            .set(field("content_type"), fileEntity.getContentType())
            .set(field("file_size"), fileEntity.getFileSize())
            .set(field("upload_date"), fileEntity.getUploadDate())
            .execute();
        
        return fileEntity;
    }
    
    public Optional<FileEntity> findById(String id) {
        Record record = dsl.select()
            .from(TABLE_NAME)
            .where(field("id").eq(id))
            .fetchOne();
        
        if (record == null) {
            return Optional.empty();
        }
        
        FileEntity entity = new FileEntity(
            record.get("id", String.class),
            record.get("file_name", String.class),
            record.get("content_type", String.class),
            record.get("file_size", Long.class),
            record.get("upload_date", LocalDateTime.class)
        );
        
        return Optional.of(entity);
    }
    
    public List<FileEntity> findAll() {
        Result<Record> result = dsl.select()
            .from(TABLE_NAME)
            .orderBy(field("upload_date").desc())
            .fetch();
        
        List<FileEntity> entities = new ArrayList<>();
        
        for (Record record : result) {
            FileEntity entity = new FileEntity(
                record.get("id", String.class),
                record.get("file_name", String.class),
                record.get("content_type", String.class),
                record.get("file_size", Long.class),
                record.get("upload_date", LocalDateTime.class)
            );
            entities.add(entity);
        }
        
        return entities;
    }
    
    public void deleteById(String id) {
        dsl.deleteFrom(table(TABLE_NAME))
            .where(field("id").eq(id))
            .execute();
    }
}
