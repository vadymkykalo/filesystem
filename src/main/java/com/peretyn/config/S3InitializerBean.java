package com.peretyn.config;

import com.peretyn.repository.FileRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;

/**
 * Initializes S3 storage on application startup
 * Creates a bucket if it doesn't exist
 */
@ApplicationScoped
public class S3InitializerBean {
    private static final Logger logger = Logger.getLogger(S3InitializerBean.class);
    
    @Inject
    MinioClient minioClient;
    
    @Inject
    FileRepository fileRepository;
    
    @ConfigProperty(name = "minio.bucket")
    String bucketName;
    
    @ConfigProperty(name = "quarkus.minio.create-bucket", defaultValue = "true")
    boolean isNeededToCreateBucket;
    
    public void onStart(@Observes StartupEvent ev) {
        try {
            logger.info("Initializing S3 storage...");
            
            // Check if the bucket exists and create it if necessary
            if (isNeededToCreateBucket) {
                boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build());
                
                if (!bucketExists) {
                    logger.info("Creating bucket: " + bucketName);
                    minioClient.makeBucket(MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
                    logger.info("Bucket successfully created: " + bucketName);
                } else {
                    logger.info("Bucket already exists: " + bucketName);
                }
            }
            
            logger.info("S3 storage initialization completed");
        } catch (MinioException e) {
            logger.error("MinIO error initializing storage: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error initializing S3 storage", e);
        }
    }
}
