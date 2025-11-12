package com.mango.products.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class PriceApiE2ETest extends BaseE2ETest {

    @Test
    void givenExistingProduct_whenAddingPrice_thenShouldReturn201() {
        // Given: Create a product
        Integer productId = createProduct("Price Test Product", "For testing prices");

        String priceRequestBody = """
            {
                "value": 99.99,
                "initDate": "2025-01-01",
                "endDate": "2025-01-31"
            }
            """;

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(priceRequestBody)
        .when()
            .post("/products/{id}/prices", productId)
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("value", equalTo(99.99f))
            .body("initDate", equalTo("2025-01-01"))
            .body("endDate", equalTo("2025-01-31"));
    }

    @Test
    void givenOverlappingPrices_whenAddingPrice_thenShouldReturn409() {
        // Given: Create product and add first price
        Integer productId = createProduct("Overlap Test Product", "Testing overlaps");

        String firstPrice = """
            {
                "value": 99.99,
                "initDate": "2025-01-01",
                "endDate": "2025-01-31"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(firstPrice)
        .when()
            .post("/products/{id}/prices", productId)
        .then()
            .statusCode(201);

        // When: Try to add overlapping price
        String overlappingPrice = """
            {
                "value": 89.99,
                "initDate": "2025-01-15",
                "endDate": "2025-02-15"
            }
            """;

        // Then
        given()
            .contentType(ContentType.JSON)
            .body(overlappingPrice)
        .when()
            .post("/products/{id}/prices", productId)
        .then()
            .statusCode(409);
    }

    @Test
    void givenNonExistingProduct_whenAddingPrice_thenShouldReturn404() {
        // Given
        String priceRequestBody = """
            {
                "value": 99.99,
                "initDate": "2025-01-01",
                "endDate": "2025-01-31"
            }
            """;

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(priceRequestBody)
        .when()
            .post("/products/{id}/prices", 99999)
        .then()
            .statusCode(404);
    }

    @Test
    void givenProductWithPrices_whenGettingHistory_thenShouldReturnAllPrices() {
        // Given: Create product and add multiple prices
        Integer productId = createProduct("History Test Product", "Testing history");

        String price1 = """
            {"value": 99.99, "initDate": "2025-01-01", "endDate": "2025-01-31"}
            """;
        String price2 = """
            {"value": 89.99, "initDate": "2025-02-01", "endDate": "2025-02-28"}
            """;

        given().contentType(ContentType.JSON).body(price1)
            .post("/products/{id}/prices", productId).then().statusCode(201);
        given().contentType(ContentType.JSON).body(price2)
            .post("/products/{id}/prices", productId).then().statusCode(201);

        // When & Then
        given()
        .when()
            .get("/products/{id}/prices", productId)
        .then()
            .statusCode(200)
            .body("prices", hasSize(2));
    }

    @Test
    void givenPriceOnDate_whenQueryingByDate_thenShouldReturnPrice() {
        // Given: Create product and add price
        Integer productId = createProduct("Date Query Test", "Testing date query");

        String priceRequestBody = """
            {"value": 99.99, "initDate": "2025-01-01", "endDate": "2025-01-31"}
            """;

        given().contentType(ContentType.JSON).body(priceRequestBody)
            .post("/products/{id}/prices", productId).then().statusCode(201);

        // When & Then: Query price for a date within range
        given()
            .queryParam("date", "2025-01-15")
        .when()
            .get("/products/{id}/prices", productId)
        .then()
            .statusCode(200)
            .body("value", notNullValue())
            .body("value", equalTo(99.99f));
    }

    // Helper method to create a product and return its ID
    private Integer createProduct(String name, String description) {
        String requestBody = String.format("""
            {
                "name": "%s",
                "description": "%s"
            }
            """, name, description);

        return given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/products")
        .then()
            .statusCode(201)
            .extract()
            .path("id");
    }
}

