package com.peretyn;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class FileResourceControllerTest {
    
    @Test
    void testGetFilesEndpoint() {
        given()
          .when().get("/files")
          .then()
             .statusCode(200)
             .contentType(ContentType.JSON);
    }
    
    @Test
    void testUploadAndDownloadFile() {
        // Create test file
        File testFile = new File("target/test-file.txt");
        try {
            testFile.createNewFile();
            java.nio.file.Files.write(testFile.toPath(), "Test content".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Загружаем файл
        Map<String, Object> formData = new HashMap<>();
        formData.put("file", testFile);
        formData.put("fileName", "test-file.txt");
        formData.put("contentType", "text/plain");
        
        String fileId = given()
            .multiPart("file", testFile)
            .multiPart("fileName", "test-file.txt")
            .multiPart("contentType", "text/plain")
            .when().post("/files")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("fileName", is("test-file.txt"))
                .body("contentType", is("text/plain"))
                .extract().path("id");
        
        // Проверяем, что файл доступен для скачивания
        given()
            .when().get("/files/{id}", fileId)
            .then()
                .statusCode(200)
                .header("Content-Type", is("text/plain"))
                .header("Content-Disposition", containsString("test-file.txt"));
        
        // Удаляем файл
        given()
            .when().delete("/files/{id}", fileId)
            .then()
                .statusCode(204);
    }
}