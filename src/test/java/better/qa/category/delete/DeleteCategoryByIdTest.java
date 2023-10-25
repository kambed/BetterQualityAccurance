package better.qa.category.delete;

import better.qa.TestBase;
import better.qa.helpers.JSONReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.apache.http.HttpStatus;
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
                .header("Content-Type", ContentType.JSON)
                .body(JSONReader.getJsonString("category/create_category.json"))
                .post(getUrlForEndpoint("categories"));
        response.then().statusCode(HttpStatus.SC_CREATED);
        categoryId = response.path("id");
    }

    @Test
    @Description("1. Basic positive tests (happy paths)")
    public void shouldDeleteExistingCategoryByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    @Description("3. Negative testing – valid input - Attempting to delete a resource that doesn’t exist")
    public void shouldNotDeleteCategoryByIdWhenIdNotExistAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("categories/%s".formatted("randomNotExistingId")))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    @Description("4. Negative testing – invalid input - Invalid authorization token")
    public void shouldNotDeleteExistingCategoryByIdWhenCorrectDataAndLoggedInAsUser() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", userToken)
                .delete(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @Description("4. Negative testing – invalid input - Missing authorization token")
    public void shouldNotDeleteExistingCategoryByIdWhenCorrectDataWhenNotLoggedIn() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .delete(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @Description("4. Negative testing – invalid input - Invalid value for endpoint parameters")
    public void shouldNotDeleteAnyCategoryWhenNoIdPassedAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("categories"))
                .then()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    @Test
    @Description("5. Destructive testing - Wrong content-type in payload")
    public void shouldNotDeleteAnyCategoryWhenWrongContentTypePassedAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.XML)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @AfterTest
    public void tearDown() {
        given().delete(getUrlForEndpoint("categories/%s".formatted(categoryId)));
    }
}
