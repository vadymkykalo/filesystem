package com.minio.service;

import com.minio.model.FileEntity;
import com.minio.repository.FileRepository;
import io.minio.*;
import io.minio.errors.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Service for file operations with S3-compatible storage
 * Handles uploading, downloading, and management of files in MinIO
 */
@ApplicationScoped
public class FileStorageService {

    @Inject
    MinioClient minioClient;
    
    @Inject
    FileRepository fileRepository;
    
    @ConfigProperty(name = "quarkus.minio.bucket")
    String bucketName;
    
    /**
     * Upload file to S3 storage and save metadata to database
     * 
     * @param data Input stream with file data
     * @param fileName Original file name
     * @param contentType MIME type of the file
     * @return File entity with metadata
     * @throws Exception if upload fails
     */
    public FileEntity uploadFile(InputStream data, String fileName, String contentType) throws Exception {
        try {
            // Read bytes from input stream to determine size
            byte[] bytes = data.readAllBytes();
            long fileSize = bytes.length;
            
            // Create new file entity
            FileEntity fileEntity = new FileEntity(fileName, contentType, fileSize);
            
            // Upload file to MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileEntity.getId())
                    .contentType(contentType)
                    .stream(new ByteArrayInputStream(bytes), fileSize, -1)
                    .build()
            );
            
            // Save metadata to database
            fileRepository.save(fileEntity);
            
            return fileEntity;
        } catch (Exception e) {
            throw new Exception("Error uploading file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Retrieve file by ID from S3 storage
     * 
     * @param id File ID to retrieve
     * @return FileDownloadResponse with file content and metadata
     * @throws Exception if download fails
     * @throws NotFoundException if file not found
     */
    public FileDownloadResponse getFileById(String id) throws Exception {
        Optional<FileEntity> fileEntityOpt = fileRepository.findById(id);
        
        if (fileEntityOpt.isEmpty()) {
            throw new NotFoundException("File with ID " + id + " not found");
        }
        
        FileEntity fileEntity = fileEntityOpt.get();
        
        try {
            GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(id)
                    .build()
            );
            
            return new FileDownloadResponse(response, fileEntity.getFileName(), fileEntity.getContentType());
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                throw new NotFoundException("File with ID " + id + " not found in storage");
            }
            throw new Exception("Error retrieving file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Error retrieving file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete file by ID from both S3 storage and database
     * 
     * @param id File ID to delete
     * @throws Exception if deletion fails
     * @throws NotFoundException if file not found
     */
    public void deleteFile(String id) throws Exception {
        Optional<FileEntity> fileEntityOpt = fileRepository.findById(id);
        
        if (fileEntityOpt.isEmpty()) {
            throw new NotFoundException("File with ID " + id + " not found");
        }
        
        try {
            // Delete file from MinIO
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(id)
                    .build()
            );
            
            // Delete metadata from database
            fileRepository.deleteById(id);
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                // If file doesn't exist in storage, just remove the database record
                fileRepository.deleteById(id);
            } else {
                throw new Exception("Error deleting file: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new Exception("Error deleting file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get list of all files in the system
     * 
     * @return List of file entities
     */
    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }
    
    /**
     * Container for file download response
     */
    public static class FileDownloadResponse {
        private final InputStream content;
        private final String fileName;
        private final String contentType;
        
        public FileDownloadResponse(InputStream content, String fileName, String contentType) {
            this.content = content;
            this.fileName = fileName;
            this.contentType = contentType;
        }
        
        public InputStream getContent() {
            return content;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public String getContentType() {
            return contentType;
        }
    }
}
