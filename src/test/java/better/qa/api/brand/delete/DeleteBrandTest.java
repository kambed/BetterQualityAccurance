package better.qa.api.brand.delete;

import better.qa.api.TestBase;
import better.qa.helpers.JSONReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class DeleteBrandTest extends TestBase {
    private String brandId;

    @BeforeMethod
    public void setUp() {
        Response response = given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "brand/brand.json",
                        Map.of(
                                "name", "Test brand",
                                "slug", "test-brand"
                        )
                ))
                .post(getUrlForEndpoint("brands"));
        response.then().statusCode(HttpStatus.SC_CREATED);
        brandId = response.path("id");
    }

    @Test
    @Description("1. Basic positive tests (happy paths)")
    public void shouldDeleteExistingBrandByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    @Description("3. Negative testing – valid input - delete non existing brand")
    public void shouldNotDeleteNonExistingBrandByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    @Description("4. Negative testing – invalid input - Missing authorization header")
    public void shouldNotDeleteExistingBrandByIdWhenCorrectDataAndNotLoggedIn() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .delete(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @Description("4. Negative testing – invalid input - Insufficient permissions")
    public void shouldNotDeleteExistingBrandByIdWhenCorrectDataAndLoggedInAsUser() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", userToken)
                .delete(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @Description("4. Negative testing – invalid input - Invalid authorization token")
    public void shouldNotDeleteExistingBrandByIdWhenCorrectDataAndInvalidToken() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", "Bearer badToken")
                .delete(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @Description("5. Destructive tests - get brand by invalid id with / character")
    public void shouldNotGetBrandByInvalidIdWithSpecialCharacters() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .delete(getUrlForEndpoint("brands/%s".formatted("invalid-id/with-special-characters")))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .onFailMessage("Requested item not found");
    }

    @AfterMethod
    public void tearDown() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("brands/%s".formatted(brandId)));
    }
}
