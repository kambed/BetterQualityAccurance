package better.qa.category.post;

import better.qa.TestBase;
import better.qa.helpers.JSONReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class CreateCategoryTest extends TestBase {

    private String categoryId;

    @Test
    @Description("1. Basic positive tests (happy paths)")
    public void shouldCreateCategoryWhenCorrectDataAndLoggedInAsAdmin() {
        Response response = given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("category/create_category.json"))
                .post(getUrlForEndpoint("categories"));
        response.then().statusCode(HttpStatus.SC_CREATED).body(
                "$", hasKey("id"),
                "$", hasKey("name"),
                "$", hasKey("slug"),
                "name", equalTo("Test category with random UUID"),
                "slug", equalTo("068cd493-e45a-4fdf-804d-1ff1a0720618")
        );
        categoryId = response.path("id");
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", Matchers.equalTo(categoryId),
                        "name", Matchers.equalTo("Test category with random UUID"),
                        "slug", Matchers.equalTo("068cd493-e45a-4fdf-804d-1ff1a0720618")
                );
    }

    @Test
    @Description("3. Negative testing – valid input - Attempting to create a resource that already exists")
    public void shouldNotCreateCategoryWhenCorrectDataAndLoggedInAsAdminAndCategoryAlreadyExists() {
        Response response = given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("category/create_category.json"))
                .post(getUrlForEndpoint("categories"));
        response.then().statusCode(HttpStatus.SC_CREATED);
        categoryId = response.path("id");
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("category/create_category.json"))
                .post(getUrlForEndpoint("categories"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    @Description("4. Negative testing – invalid input - Missing required parameters")
    public void shouldNotCreateCategoryWhenCorrectDataAndLoggedInAsAdminAndMissingRequiredParameters() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("category/create_category_missing_name.json"))
                .post(getUrlForEndpoint("categories"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    @Description("4. Negative testing – invalid input - Empty body")
    public void shouldNotCreateCategoryWhenCorrectDataAndLoggedInAsAdminAndEmptyBody() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .post(getUrlForEndpoint("categories"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    @Description("5. Negative testing – invalid input - Wrong content-type in payload")
    public void shouldNotCreateCategoryWhenCorrectDataAndLoggedInAsAdminAndWrongContentTypeInPayload() {
        given()
                .when()
                .header("Content-Type", ContentType.XML)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("category/create_category.json"))
                .post(getUrlForEndpoint("categories"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @AfterMethod
    public void tearDown() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .delete(getUrlForEndpoint("categories/%s".formatted(categoryId)));
    }

}
