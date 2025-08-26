package com.minio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
public class FileEntity {
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;
    
    @Column(name = "file_size", nullable = false)
    private long fileSize;
    
    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;
    
    public FileEntity() {
    }
    
    public FileEntity(String fileName, String contentType, long fileSize) {
        this.id = UUID.randomUUID().toString();
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadDate = LocalDateTime.now();
    }

    public FileEntity(String id, String fileName, String contentType, long fileSize, LocalDateTime uploadDate) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public LocalDateTime getUploadDate() {
        return uploadDate;
    }
    
    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
}
