package com.minio.controller;

import com.minio.model.FileEntity;
import com.minio.service.FileStorageService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.tika.Tika;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * REST controller for file operations with S3 storage
 */
@Path("/files")
@Tag(name = "Files API", description = "API for file operations using S3-compatible storage")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FileResourceController {
    @Inject
    FileStorageService fileStorageService;

    /**
     * Get a list of all files in the system
     *
     * @return Response with list of files
     */
    @GET
    @Operation(summary = "Get all files")
    @APIResponse(responseCode = "200", description = "List of all files", 
                content = @Content(mediaType = MediaType.APPLICATION_JSON))
    public Response getAllFiles() {
        List<FileEntity> files = fileStorageService.getAllFiles();
        return Response.ok(files).build();
    }

    /**
     * Get file by its ID
     *
     * @param id File ID
     * @return Response with file content
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get file by ID")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @APIResponse(responseCode = "200", description = "File successfully retrieved")
    @APIResponse(responseCode = "404", description = "File not found")
    public Response getFileById(@PathParam("id") String id) {
        try {
            FileStorageService.FileDownloadResponse downloadResponse = fileStorageService.getFileById(id);
            
            return Response.ok(downloadResponse.content())
                    .header("Content-Disposition", "attachment; filename=\"" + downloadResponse.fileName() + "\"")
                    .header("Content-Type", downloadResponse.contentType())
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("File not found"))
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity(new ErrorResponse("Error retrieving file: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Upload a new file to storage
     *
     * @param form Upload form with file data
     * @return Response with created file entity
     */
    @POST
    @Operation(summary = "Upload new file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @APIResponse(responseCode = "201", description = "File successfully uploaded", 
                content = @Content(mediaType = MediaType.APPLICATION_JSON, 
                schema = @Schema(implementation = FileEntity.class)))
    public Response uploadFile(@MultipartForm FileUploadForm form) {
        try {

            byte[] fileBytes = form.getFile().readAllBytes();
            String fileName = form.getFileName();

            String contentType = detectContentType(fileBytes, fileName);

            InputStream fileInputStream = new ByteArrayInputStream(fileBytes);
            
            FileEntity fileEntity = fileStorageService.uploadFile(fileInputStream, fileName, contentType);
            return Response.status(Response.Status.CREATED).entity(fileEntity).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity(new ErrorResponse("Error uploading file: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete file by its ID
     *
     * @param id File ID to delete
     * @return Response with deletion status
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete file by ID")
    @APIResponse(responseCode = "204", description = "File successfully deleted")
    @APIResponse(responseCode = "404", description = "File not found")
    public Response deleteFile(@PathParam("id") String id) {
        try {
            fileStorageService.deleteFile(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("File not found"))
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity(new ErrorResponse("Error deleting file: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Detect content type using Tika, with fallback to extension-based detection
     * 
     * @param fileBytes The file content as byte array
     * @param fileName The original file name (used as fallback)
     * @return Detected MIME type
     */
    private String detectContentType(byte[] fileBytes, String fileName) {
        try {
            // Use Tika to detect content type from actual bytes
            Tika tika = new Tika();
            String detectedType = tika.detect(fileBytes);

            if (detectedType.equals("application/octet-stream") || detectedType.equals("text/plain")) {
                String extensionType = determineContentTypeFromExtension(fileName);

                if (!extensionType.equals(MediaType.APPLICATION_OCTET_STREAM)) {
                    return extensionType;
                }
            }
            
            return detectedType;
        } catch (Exception e) {
            return determineContentTypeFromExtension(fileName);
        }
    }
    
    /**
     * Fallback method to determine content type based on file extension
     * 
     * @param fileName File name with extension
     * @return MIME type for the file based on extension
     */
    private String determineContentTypeFromExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        return switch (extension) {
            // Images
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";

            // Documents
            case "pdf" -> "application/pdf";
            case "doc", "docx" -> "application/msword";
            case "xls", "xlsx" -> "application/vnd.ms-excel";
            case "ppt", "pptx" -> "application/vnd.ms-powerpoint";
            case "txt" -> "text/plain";
            case "html", "htm" -> "text/html";
            case "xml" -> "application/xml";
            case "json" -> "application/json";

            // Audio
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "ogg" -> "audio/ogg";

            // Video
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "webm" -> "video/webm";
            case "mkv" -> "video/x-matroska";

            // Archives
            case "zip" -> "application/zip";
            case "rar" -> "application/x-rar-compressed";
            case "7z" -> "application/x-7z-compressed";
            case "tar" -> "application/x-tar";
            case "gz" -> "application/gzip";
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
    
    /**
     * Form for file upload with multipart data
     */
    public static class FileUploadForm {
        private InputStream file;
        private String fileName;

        public InputStream getFile() {
            return file;
        }

        @FormParam("file")
        public void setFile(InputStream file) {
            this.file = file;
        }

        public String getFileName() {
            return fileName;
        }

        @FormParam("fileName")
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
    
    /**
     * Error response object
     */
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
