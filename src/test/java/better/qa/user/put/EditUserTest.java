package better.qa.user.put;

import better.qa.TestBase;
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
import static org.hamcrest.Matchers.equalTo;

public class EditUserTest extends TestBase {

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
    public void shouldEditUserEmailByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "user/edit_user_email.json",
                        Map.of("email", "editedEmail@gmail.com")
                ))
                .put(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .get(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", equalTo(userId),
                        "email", equalTo("editedEmail@gmail.com")
                );
    }

    @Test
    @Description("2. Positive + optional parameters")
    public void shouldEditUserAllParametersByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "user/edit_user_all_data.json",
                        Map.of(
                                "first_name", "EditedFirst",
                                "last_name", "EditedLast",
                                "address", "EditedAddress",
                                "city", "EditedCity",
                                "state", "EditedState",
                                "country", "EditedCountry",
                                "postcode", "EditedPost",
                                "phone", "123321123",
                                "dob", "2222-01-01",
                                "email", "edited@gmail.edited"
                        )
                ))
                .put(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));

        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .get(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", equalTo(userId),
                        "first_name", equalTo("EditedFirst"),
                        "last_name", equalTo("EditedLast"),
                        "address", equalTo("EditedAddress"),
                        "city", equalTo("EditedCity"),
                        "state", equalTo("EditedState"),
                        "country", equalTo("EditedCountry"),
                        "postcode", equalTo("EditedPost"),
                        "phone", equalTo("123321123"),
                        "dob", equalTo("2222-01-01"),
                        "email", equalTo("edited@gmail.edited")
                );
    }

    @Test
    @Description("3. Negative tests - valid input - edit user by non-existing id")
    public void shouldNotEditUserByNonExistingId() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "user/edit_user_all_data.json",
                        Map.of(
                                "first_name", "EditedFirst",
                                "last_name", "EditedLast",
                                "address", "EditedAddress",
                                "city", "EditedCity",
                                "state", "EditedState",
                                "country", "EditedCountry",
                                "postcode", "EditedPost",
                                "phone", "123321123",
                                "dob", "2222-01-01",
                                "email", "edited@gmail.edited"
                        )
                ))
                .put(getUrlForEndpoint("users/%s".formatted("non-existing-id")))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(false));
    }

    @Test
    @Description("4. Negative tests - invalid input - edit user by missed id")
    public void shouldNotEditUserMissedId() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "user/edit_user_all_data.json",
                        Map.of(
                                "first_name", "EditedFirst",
                                "last_name", "EditedLast",
                                "address", "EditedAddress",
                                "city", "EditedCity",
                                "state", "EditedState",
                                "country", "EditedCountry",
                                "postcode", "EditedPost",
                                "phone", "123321123",
                                "dob", "2222-01-01",
                                "email", "edited@gmail.edited"
                        )
                ))
                .put(getUrlForEndpoint("users/%s".formatted(null)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(false));
    }

    @Test
    @Description("5. Destructive tests - invalid input - edit user by not valid body")
    public void shouldNotEditUserMissedBody() {
        System.out.printf(adminToken);
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body("EMPTY BODY")
                .put(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                .body("email[0]", equalTo("The email field is required."))
                .body("first_name[0]", equalTo("The first name field is required."))
                .body("last_name[0]", equalTo("The last name field is required."))
                .body("address[0]", equalTo("The address field is required."))
                .body("city[0]", equalTo("The city field is required."))
                .body("country[0]", equalTo("The country field is required."));
    }

    @Test
    @Description("6. Security testing - edit user by not logged in user")
    public void shouldNotEditUserWhenNotLoggedIn() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .body(JSONReader.getJsonString(
                        "user/edit_user_all_data.json",
                        Map.of(
                                "first_name", "EditedFirst",
                                "last_name", "EditedLast",
                                "address", "EditedAddress",
                                "city", "EditedCity",
                                "state", "EditedState",
                                "country", "EditedCountry",
                                "postcode", "EditedPost",
                                "phone", "123321123",
                                "dob", "2222-01-01",
                                "email", "emaail@Gmail.com"
                        )
                ))
                .put(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @Description("6. Security testing - edit user by logged in user")
    public void shouldNotEditUserWhenLoggedInAsUser() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", userToken)
                .body(JSONReader.getJsonString(
                        "user/edit_user_all_data.json",
                        Map.of(
                                "first_name", "EditedFirst",
                                "last_name", "EditedLast",
                                "address", "EditedAddress",
                                "city", "EditedCity",
                                "state", "EditedState",
                                "country", "EditedCountry",
                                "postcode", "EditedPost",
                                "phone", "123321123",
                                "dob", "2222-01-01",
                                "email", "edited@Gmail.com"
                        )
                ))
                .put(getUrlForEndpoint("users/%s".formatted(userId)))
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
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
