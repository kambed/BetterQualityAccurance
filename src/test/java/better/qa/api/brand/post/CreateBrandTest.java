package better.qa.api.brand.post;

import better.qa.api.TestBase;
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

public class CreateBrandTest extends TestBase {
    private String brandId;

    @Test
    @Description("1. Basic positive tests (happy paths)")
    public void shouldCreateBrandWhenCorrectDataAndLoggedInAsAdmin() {
        ValidatableResponse body = given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "brand/brand.json",
                        Map.of(
                                "name", "New test brand",
                                "slug", "new-test-brand"
                        )
                ))
                .post(getUrlForEndpoint("brands"))
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body(
                        "id", notNullValue(),
                        "name", equalTo("New test brand"),
                        "slug", equalTo("new-test-brand")
                );
        brandId = body.extract().path("id");
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", equalTo(brandId),
                        "name", equalTo("New test brand"),
                        "slug", equalTo("new-test-brand")
                );
    }

    @Test
    @Description("3. Negative testing – valid input - Trying to create a brand with already existing slug")
    public void shouldNotCreateBrandWhenCorrectDataAndLoggedInAsAdminAndBrandAlreadyExists() {
        ValidatableResponse body = given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "brand/brand.json",
                        Map.of(
                                "name", "New test brand",
                                "slug", "new-test-brand"
                        )
                ))
                .post(getUrlForEndpoint("brands"))
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body(
                        "id", notNullValue(),
                        "name", equalTo("New test brand"),
                        "slug", equalTo("new-test-brand")
                );
        brandId = body.extract().path("id");
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "brand/brand.json",
                        Map.of(
                                "name", "New test brand",
                                "slug", "new-test-brand"
                        )
                ))
                .post(getUrlForEndpoint("brands"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                .body("slug[0]", equalTo("A brand already exists with this slug."));
    }

    @Test
    @Description("4. Negative testing – invalid input - Missing required parameter name")
    public void shouldNotCreateBrandWhenRequiredParameterNameIsMissing() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("brand/brand_only_slug.json"))
                .post(getUrlForEndpoint("brands"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                .body("name[0]", equalTo("The name field is required."));
    }

    @Test
    @Description("4. Negative testing – invalid input - Missing required parameter slug")
    public void shouldNotCreateBrandWhenRequiredParameterSlugIsMissing() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString("brand/brand_only_name.json"))
                .post(getUrlForEndpoint("brands"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                .body("slug[0]", equalTo("The slug field is required."));
    }

    @Test
    @Description("5. Destructive testing – invalid input - XML instead of JSON")
    public void shouldNotCreateBrandWhenCorrectDataAndLoggedInAsAdminAndWrongContentTypeInPayload() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(
                        """
                            <?xml version="1.0" encoding="UTF-8"?>
                            <brand>
                                <name>Test brand</name>
                                <slug>test-brand</slug>
                            </brand>
                        """
                )
                .post(getUrlForEndpoint("brands"))
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
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
