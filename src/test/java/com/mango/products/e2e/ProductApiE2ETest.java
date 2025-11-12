package com.mango.products.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class ProductApiE2ETest extends BaseE2ETest {

    @Test
    void givenValidProductData_whenCreatingProduct_thenShouldReturn201() {
        // Given
        String requestBody = """
            {
                "name": "E2E Product",
                "description": "Product for testing"
            }
            """;

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/products")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("E2E Product"))
            .body("description", equalTo("Product for testing"));
    }

    @Test
    void givenDuplicateProductName_whenCreatingProduct_thenShouldReturn409() {
        // Given
        String requestBody = """
            {
                "name": "Duplicate Product",
                "description": "First product"
            }
            """;

        // Create first product
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/products")
        .then()
            .statusCode(201);

        // When & Then: Try to create duplicate
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/products")
        .then()
            .statusCode(409);
    }

    @Test
    void givenExistingProduct_whenGettingById_thenShouldReturn200() {
        // Given: Create a product first
        String createRequestBody = """
            {
                "name": "Get Test Product",
                "description": "Test description"
            }
            """;

        Integer productId = given()
            .contentType(ContentType.JSON)
            .body(createRequestBody)
        .when()
            .post("/products")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        // When & Then: Get the product
        given()
        .when()
            .get("/products/{id}", productId)
        .then()
            .statusCode(200)
            .body("id", equalTo(productId))
            .body("name", equalTo("Get Test Product"))
            .body("description", equalTo("Test description"));
    }

    @Test
    void givenNonExistingProductId_whenGettingProduct_thenShouldReturn404() {
        // When & Then
        given()
        .when()
            .get("/products/{id}", 99999)
        .then()
            .statusCode(404);
    }


    @Test
    void givenProductWithoutDescription_whenCreating_thenShouldReturn201() {
        // Given
        String requestBody = """
            {
                "name": "Product Without Description"
            }
            """;

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/products")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("Product Without Description"));
    }
}

