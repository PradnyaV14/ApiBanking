package com.productManagement.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.*;
import org.jboss.logging.Logger;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Test file for performing unit test cases on reactive endpoints.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductResourceTest {

    private static Long productId;
    private static final Logger LOG = Logger.getLogger(ProductResourceTest.class);


    /**
     * To retrieve all products.
     */
    @Test
    @Order(1)
    public void testGetAllProducts() {
    	LOG.info("Fetching all products");
        given()
          .when().get("/products")
          .then()
             .statusCode(200)
             .contentType(ContentType.JSON);
    }

    /**
     * To add a new product.
     */
    @Test
    @Order(2)
    public void testCreateProduct() {
    	LOG.info("Creating a new product");
        String productJson = """
            {
                "name": "Laptop",
                "description": "High-end gaming laptop",
                "price": 1499.99,
                "quantity": 5
            }
            """;

        Response response = given()
          .contentType(ContentType.JSON)
          .body(productJson)
          .when().post("/products")
          .then()
             .statusCode(201)
             .contentType(ContentType.JSON)
             .body("name", equalTo("Laptop"))
             .body("price", hasToString("1499.99"))
             .extract().response();

        productId = response.jsonPath().getLong("id");
        LOG.info("Added Product with id: " + productId + "\nDetails:\n" + response.asPrettyString());

        Assertions.assertNotNull(productId, "Product ID should not be null");
    }

    /**
     * To fetch a specific product by ID.
     */
    @Test
    @Order(3)
    public void testGetProductById() {
        Assertions.assertNotNull(productId, "Product ID should not be null");
        LOG.info("Fetching product with id: " + productId);

        given()
          .pathParam("id", productId)
          .when().get("/products/{id}")
          .then()
             .statusCode(200)
             .contentType(ContentType.JSON)
             .body("id", is(productId.intValue()))
             .body("name", equalTo("Laptop"));
    }

    /**
     * To update an existing product's details.
     */
    @Test
    @Order(4)
    public void testUpdateProduct() {
        Assertions.assertNotNull(productId, "Product ID should not be null");
        LOG.info("Updating product with id: " + productId);
        String updatedProductJson = """
            {
                "name": "Gaming Laptop",
                "description": "Updated high-end gaming laptop",
                "price": 1599.99,
                "quantity": 3
            }
            """;

        given()
          .pathParam("id", productId)
          .contentType(ContentType.JSON)
          .body(updatedProductJson)
          .when().put("/products/{id}")
          .then()
             .statusCode(200)
             .contentType(ContentType.JSON)
             .body("name", equalTo("Gaming Laptop"))
             .body("price", hasToString("1599.99"));
    }

    /**
     * To delete a product based on ID.
     */
    @Test
    @Order(5)
    public void testDeleteProduct() {
        Assertions.assertNotNull(productId, "Product ID should not be null");
        LOG.info("Deleting product with id: " + productId);
        
        given()
          .pathParam("id", productId)
          .when().delete("/products/{id}")
          .then()
             .statusCode(204);
    }
    /**
     * returns true if quantity >= 2
     */
    @Test
    @Order(6)
    public void testCheckStockAvailability() {
        Assertions.assertNotNull(productId, "Product ID should not be null");
        LOG.info("Checking stock availability for product id: " + productId);
        given()
          .pathParam("id", productId)
          .queryParam("count", 2)
          .when().get("/products/{id}/stock")
          .then()
             .statusCode(200)
             .contentType(ContentType.JSON)
             .body(equalTo("true")); 
    }
}
