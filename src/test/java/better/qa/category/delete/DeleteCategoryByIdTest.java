package better.qa.category.delete;

import better.qa.TestBase;
import better.qa.helpers.JSONReader;
import io.restassured.response.Response;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class DeleteCategoryByIdTest extends TestBase {

    private String categoryId;

    @BeforeTest
    public void setUp() {
        Response response = given()
                .when()
                .header("Content-Type", "application/json")
                .body(JSONReader.getJsonString("category/create_category.json"))
                .post(getUrlForEndpoint("categories"));
        response.then().statusCode(201);
        categoryId = response.path("id");
    }

    //1. Basic positive tests (happy paths)
    @Test
    public void shouldDeleteExistingCategoryByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", "application/json")
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(204);
    }

    //3. Negative testing – valid input - Attempting to delete a resource that doesn’t exist
    @Test
    public void shouldNotDeleteCategoryByIdWhenIdNotExistAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", "application/json")
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("categories/%s".formatted("randomNotExistingId")))
                .then()
                .statusCode(422);
    }

    //4. Negative testing – invalid input - Invalid authorization token
    @Test
    public void shouldNotDeleteExistingCategoryByIdWhenCorrectDataAndLoggedInAsUser() {
        given()
                .when()
                .header("Content-Type", "application/json")
                .header("Authorization", userToken)
                .delete(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(403);
    }

    //4. Negative testing – invalid input - Missing authorization token
    @Test
    public void shouldNotDeleteExistingCategoryByIdWhenCorrectDataWhenNotLoggedIn() {
        given()
                .when()
                .header("Content-Type", "application/json")
                .delete(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(401);
    }

    //4. Negative testing – invalid input - Invalid value for endpoint parameters
    @Test
    public void shouldNotDeleteAnyCategoryWhenNoIdPassedAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", "application/json")
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("categories"))
                .then()
                .statusCode(405);
    }

    //5. Destructive testing - Wrong content-type in payload
    @Test
    public void shouldNotDeleteAnyCategoryWhenWrongContentTypePassedAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", "application/xml")
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(422);
    }

    @AfterTest
    public void tearDown() {
        given().delete(getUrlForEndpoint("categories/%s".formatted(categoryId)));
    }
}
