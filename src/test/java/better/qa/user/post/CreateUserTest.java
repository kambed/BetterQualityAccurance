package better.qa.user.post;

import better.qa.TestBase;
import better.qa.helpers.JSONReader;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jdk.jfr.Description;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest extends TestBase {

    private String userId;

    @Test
    @Description("1. Basic positive tests (happy paths)")
    public void shouldCreateUserWhenCorrectData() {
        ValidatableResponse body = given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .body(JSONReader.getJsonString(
                        "user/create_user.json",
                        Map.of(
                                "email", "ExampleEmail@gmail.com"
                        )
                ))
                .post(getUrlForEndpoint("users/register"))
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body(
                        "email", equalTo("ExampleEmail@gmail.com"),
                        "first_name", equalTo("John"),
                        "last_name", equalTo("Doe")
                );
        userId = body.extract().path("id");
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
    @Description("3. Negative testing – valid input - Trying to create a user with already existing email")
    public void shouldNotCreateUserWhenCorrectDataUserWithGivenEmailAlreadyExists() {
        ValidatableResponse body = given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .body(JSONReader.getJsonString(
                        "user/create_user.json",
                        Map.of(
                                "email", "ExampleEmail@gmail.com"
                        )
                ))
                .post(getUrlForEndpoint("users/register"))
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body(
                        "id", notNullValue(),
                        "email", equalTo("ExampleEmail@gmail.com"),
                        "first_name", equalTo("John"),
                        "last_name", equalTo("Doe")
                );
        userId = body.extract().path("id");
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .body(JSONReader.getJsonString(
                        "user/create_user.json",
                        Map.of(
                                "email", "ExampleEmail@gmail.com"
                        )
                ))
                .post(getUrlForEndpoint("users/register"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                .body("email[0]", equalTo("A customer with this email address already exists."));
    }

    @Test
    @Description("4. Negative testing – invalid input - Missing required parameter name")
    public void shouldNotCreateUserWhenRequiredParametersAreMissing() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .body(JSONReader.getJsonString("user/create_user_without_password.json"))
                .post(getUrlForEndpoint("users/register"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                .body("password[0]", equalTo("The password field is required."))
                .body("email[0]", equalTo("The email field is required."))
                .body("first_name[0]", equalTo("The first name field is required."))
                .body("last_name[0]", equalTo("The last name field is required."));
    }

    @Test
    @Description("5. Destructive testing – invalid input - XML instead of JSON")
    public void shouldNotCreateUserWhenCorrectDataButWrongContentTypeInPayload() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .body(
                        """
                            <?xml version="1.0" encoding="UTF-8"?>
                            <user>
                                <first_name>John</first_name>
                                <last_name>Doe</last_name>
                                <address>Street 1</address>
                                <city>City</city>
                                <state>State</state>
                                <country>Country</country>
                                <postcode>1234AA</postcode>
                                <phone>0987654321</phone>
                                <dob>1970-01-01</dob>
                                <email>Email@email.com</email>
                                <password>super-secret</password>
                            </user>
                        """
                )
                .post(getUrlForEndpoint("users/register"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
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
