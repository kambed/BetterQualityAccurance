package better.qa;

import io.github.cdimascio.dotenv.Dotenv;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

public class TestBase {
    protected String apiUrl;

    @BeforeSuite
    public void setBaseUri () {
        Dotenv dotenv = Dotenv.load();
        apiUrl = dotenv.get("API_URL");
    }

    public String getUrlForEndpoint(String endpoint) {
        return "%s/%s".formatted(apiUrl, endpoint);
    }
}
