package better.qa.api.brand.put;

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
import static org.hamcrest.Matchers.equalTo;

public class EditBrandTest extends TestBase {
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
    public void shouldEditBrandNameByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "brand/brand_only_name.json",
                        Map.of("name", "Edited brand")
                ))
                .put(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", equalTo(brandId),
                        "name", equalTo("Edited brand"),
                        "slug", equalTo("test-brand")
                );
    }

    @Test
    @Description("2. Positive + optional parameters")
    public void shouldEditBrandAllParametersByIdWhenCorrectDataAndLoggedInAsAdmin() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "brand/brand.json",
                        Map.of(
                                "name", "Edited test brand",
                                "slug", "edited-test-brand"
                        )
                ))
                .put(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", equalTo(brandId),
                        "name", equalTo("Edited test brand"),
                        "slug", equalTo("edited-test-brand")
                );
    }

    @Test
    @Description("3. Negative tests - valid input - edit brand by non-existing id")
    public void shouldNotEditBrandByNonExistingId() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "brand/brand.json",
                        Map.of(
                                "name", "Edited test brand",
                                "slug", "edited-test-brand"
                        )
                ))
                .put(getUrlForEndpoint("brands/%s".formatted("non-existing-id")))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(false));
    }

    @Test
    @Description("4. Negative tests - invalid input - edit brand by missed id")
    public void shouldNotEditBrandMissedId() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "brand/brand.json",
                        Map.of(
                                "name", "Edited test brand",
                                "slug", "edited-test-brand"
                        )
                ))
                .put(getUrlForEndpoint("brands/%s".formatted(null)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(false));
    }

    @Test
    @Description("5. Destructive tests - invalid input - edit brand by not valid body")
    public void shouldNotEditBrandMissedBody() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body("bleble")
                .put(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(false));
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
