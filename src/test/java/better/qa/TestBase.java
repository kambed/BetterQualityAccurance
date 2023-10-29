package better.qa;

import better.qa.helpers.JSONReader;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import static io.restassured.RestAssured.given;

public class TestBase {
    protected String apiUrl;
    protected String adminToken;
    protected String userToken;

    @BeforeClass
    protected void setBaseUri() {
        Dotenv dotenv = Dotenv.load();
        apiUrl = dotenv.get("API_URL");
    }

    @BeforeClass
    protected void setTokens() {
        Response response = given()
                .header("Content-Type", ContentType.JSON)
                .body(JSONReader.getJsonString("admin_credentials.json"))
                .post(getUrlForEndpoint("users/login"));
        response.then().statusCode(HttpStatus.SC_OK);
        adminToken = "Bearer " + response.path("access_token");
        response = given()
                .header("Content-Type", ContentType.JSON)
                .body(JSONReader.getJsonString("user_credentials.json"))
                .post(getUrlForEndpoint("users/login"));
        response.then().statusCode(HttpStatus.SC_OK);
        userToken = "Bearer " + response.path("access_token");
    }

    protected String getUrlForEndpoint(String endpoint) {
        return "%s/%s".formatted(apiUrl, endpoint);
    }
}
