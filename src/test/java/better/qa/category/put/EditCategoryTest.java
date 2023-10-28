package better.qa.category.put;

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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class EditCategoryTest extends TestBase {

    private String categoryId;

    @BeforeMethod
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
    public void shouldEditCategoryNameByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("category/edit_category_name.json"))
                .put(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "success", equalTo(true)
                );
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", Matchers.equalTo(categoryId),
                        "name", Matchers.equalTo("Edited test category with random UUID"),
                        "slug", Matchers.equalTo("068cd493-e45a-4fdf-804d-1ff1a0720618")
                );
    }

    @Test
    @Description("2. Positive + optional parameters")
    public void shouldEditCategoryAllParametersByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("category/edit_category.json"))
                .put(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "success", equalTo(true)
                );
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", Matchers.equalTo(categoryId),
                        "name", Matchers.equalTo("Edited test category with random UUID"),
                        "slug", Matchers.equalTo("068cd493-e45a-4fdf-804d-1ff1a0720619")
                );
    }

    @Test
    @Description("3. Negative testing – valid input - attempting to edit a resource that does not exist")
    public void shouldNotEditCategoryWhenCategoryWithThisIdAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("category/edit_category_name.json"))
                .put(getUrlForEndpoint("categories/%s".formatted("non-existing-category")))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "success", equalTo(false)
                );
    }

    @Test
    @Description("4. Negative testing – invalid input - attempting to edit a resource with with no body")
    public void shouldNotEditCategoryWhenNoBodyAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .put(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "success", equalTo(false)
                );
    }

    @Test
    @Description("5. Negative testing – invalid input - Malformed content in request")
    public void shouldNotEditCategoryWhenMalformedContentAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("category/edit_category_wrong_structure.json"))
                .put(getUrlForEndpoint("categories/%s".formatted(categoryId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "success", equalTo(false)
                );
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
