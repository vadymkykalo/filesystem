package com.minio;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

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
}