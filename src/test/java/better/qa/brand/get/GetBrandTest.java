package better.qa.brand.get;

import better.qa.TestBase;
import better.qa.helpers.JSONReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.apache.http.HttpStatus;
import org.testng.annotations.*;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class GetBrandTest extends TestBase {
    private String brandId;

    @BeforeMethod
    public void setUp() {
        Response response = given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", adminToken)
                .body(JSONReader.getJsonString(
                        "brand/create_brand.json",
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
    public void shouldGetAllBrands() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("brands"))
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Description("2. Positive + optional parameters")
    public void shouldGetBrandById() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("brands/%s".formatted(brandId)))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(
                        "id", equalTo(brandId),
                        "name", equalTo("Test brand"),
                        "slug", equalTo("test-brand")
                );
    }

    @Test
    @Description("3. Negative tests - valid input - get brand by non-existing id")
    public void shouldNotGetBrandByNonExistingId() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("brands/%s".formatted("non-existing-id")))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .onFailMessage("Requested item not found");
    }

    @Test
    @Description("4. Negative tests - invalid input - get brand by missed id")
    public void shouldNotGetBrandMissedId() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("brands/%s".formatted(null)))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .onFailMessage("Requested item not found");
    }

    @Test
    @Description("5. Destructive tests - get brand by invalid id with / character")
    public void shouldNotGetBrandByInvalidIdWithSpecialCharacters() {
        given()
                .when()
                .header("Content-Type", ContentType.JSON)
                .get(getUrlForEndpoint("brands/%s".formatted("invalid-id/with-special-characters")))
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
