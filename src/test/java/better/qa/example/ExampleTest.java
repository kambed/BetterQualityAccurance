package better.qa.example;

import better.qa.helpers.JSONReader;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ExampleTest {

    @Test
    public void testStatusCode() {
        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/posts/1")
                .then()
                .statusCode(200);
    }

    @Test
    public void testResponseHeader() {
        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/posts/1")
                .then()
                .assertThat()
                .header("content-type", equalTo("application/json; charset=utf-8"));
    }

    @Test
    public void testResponseBody() {
        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/posts/1")
                .then()
                .assertThat()
                .body("userId", equalTo(1))
                .body("id", equalTo(1))
                .body("title", equalTo("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"))
                .body("body", equalTo("quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"));
    }

    @Test
    public void jsonReaderTest() {
        Map<String, Object> jsonMap = JSONReader.readJSONFile("example.json");
        assertThat(jsonMap.get("name").equals("Thomas"), is(true));
    }
}
