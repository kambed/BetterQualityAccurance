package better.qa.category.get;

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
import static org.hamcrest.Matchers.hasSize;

public class GetCategoryTest extends TestBase {

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
    public void shouldGetAllCategories() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("categories"))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", Matchers.not(hasSize(0)));
    }

    @Test
    @Description("2. Positive + optional parameters")
    public void shouldGetCategoryById() {
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
    @Description("3. Negative testing – valid input - Attempting to get a resource that does not exist")
    public void shouldNotGetCategoryWhenCategoryWithThisIdDoesNotExist() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("categories/%s".formatted("non-existing-category")))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Description("4. Negative testing – invalid input - Invalid value for endpoint parameters")
    public void shouldNotGetCategoryWhenCategoryIdIsInvalid() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("categories/%s".formatted(null)))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Description("5. Destructive testing - Malformed content in request - too long ID")
    public void shouldNotGetCategoryWithInvalidIDWhichIs100000CharactersLong() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("categories/%s".formatted("x".repeat(10000))))
                .then()
                .statusCode(HttpStatus.SC_REQUEST_URI_TOO_LONG);
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
