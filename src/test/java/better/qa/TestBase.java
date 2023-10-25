package better.qa;

import better.qa.helpers.JSONReader;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import static io.restassured.RestAssured.given;

public class TestBase {
    protected String apiUrl;
    protected String adminToken;
    protected String userToken;

    @BeforeSuite
    public void setBaseUri() {
        Dotenv dotenv = Dotenv.load();
        apiUrl = dotenv.get("API_URL");
    }

    @BeforeSuite
    public void setTokens() {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(JSONReader.getJsonString("admin_credentials.json"))
                .post(getUrlForEndpoint("users/login"));
        response.then().statusCode(200);
        adminToken = "Bearer " + response.path("access_token");
        response = given()
                .header("Content-Type", "application/json")
                .body(JSONReader.getJsonString("user_credentials.json"))
                .post(getUrlForEndpoint("users/login"));
        response.then().statusCode(200);
        userToken = "Bearer " + response.path("access_token");
    }

    public String getUrlForEndpoint(String endpoint) {
        return "%s/%s".formatted(apiUrl, endpoint);
    }
}
