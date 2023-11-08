package better.qa.user.get;

import better.qa.TestBase;
import better.qa.helpers.JSONReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class GetUserTest extends TestBase {

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
    public void shouldGetAllUsers() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .get(getUrlForEndpoint("users"))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", Matchers.not(hasSize(0)));
    }

    @Test
    @Description("2. Positive + optional parameters")
    public void shouldGetUserById() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .get(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", equalTo(userId),
                        "email", equalTo("ExampleEmail@gmail.com"),
                        "first_name", equalTo("John"),
                        "last_name", equalTo("Doe")
                );
    }

    @Test
    @Description("3. Negative testing – valid input - Attempting to get a resource that does not exist")
    public void shouldNotGetUserWhenUserWithThisIdDoesNotExist() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .get(getUrlForEndpoint("users/%s".formatted("non-existing-user")))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Description("4. Negative testing – invalid input - Invalid value for endpoint parameters")
    public void shouldNotGetUserWhenUserIdIsInvalid() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("categories/%s".formatted(null)))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Description("5. Destructive testing - Malformed content in request - too long ID")
    public void shouldNotGetUserWithInvalidIDWhichIs100000CharactersLong() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("users/%s".formatted("x".repeat(10000))))
                .then()
                .statusCode(HttpStatus.SC_REQUEST_URI_TOO_LONG);
    }

    @Test
    @Description("6. Security testing - Attempting to access a resource without authorization")
    public void shouldNotGetUserWhenUserIsNotAuthorized() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
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
