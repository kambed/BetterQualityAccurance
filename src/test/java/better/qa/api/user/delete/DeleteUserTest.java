package better.qa.api.user.delete;

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

public class DeleteUserTest extends TestBase {

    private String userId;

    @BeforeMethod
    public void setUp() {
        Response response = given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .body(JSONReader.getJsonString("user/create_user.json"))
                .body(JSONReader.getJsonString(
                        "user/create_user.json",
                        Map.of(
                                "email", "ExampleEmail@gmail.com"
                        )
                ))
                .post(getUrlForEndpoint("users/register"));
        response.then().statusCode(HttpStatus.SC_CREATED);
        userId = response.path("id");
    }

    @Test
    @Description("1. Basic positive tests (happy paths)")
    public void shouldDeleteExistingUserByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    @Description("3. Negative testing – valid input - delete non existing user")
    public void shouldNotDeleteNonExistingUserByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    @Description("4. Negative testing – invalid input - Missing authorization header")
    public void shouldNotDeleteExistingUserByIdWhenCorrectDataAndNotLoggedIn() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .delete(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @Description("4. Negative testing – invalid input - Insufficient permissions")
    public void shouldNotDeleteExistingUserByIdWhenCorrectDataAndLoggedInAsUser() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", userToken)
                .delete(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @Description("4. Negative testing – invalid input - Invalid authorization token")
    public void shouldNotDeleteExistingUserByIdWhenCorrectDataAndInvalidToken() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", "Bad Token Case")
                .delete(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @Description("5. Destructive tests - get user by invalid id with / character")
    public void shouldNotDeleteUserByInvalidIdWithSpecialCharacters() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("users/%s".formatted("invalid-id/with-special-characters")))
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
                .delete(getUrlForEndpoint("users/%s".formatted(userId)));
    }
}
