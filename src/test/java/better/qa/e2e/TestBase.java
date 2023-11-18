package better.qa.e2e;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeClass;

public class TestBase {

    protected static final String NAME = "Practice Software Testing - Toolshop - v5.0";
    protected String WEB_URL;
    protected String adminEmail;
    protected String adminPassword;
    protected WebDriver driver;

    @BeforeClass
    protected void setBaseUri() {
        Dotenv dotenv = Dotenv.load();
        WEB_URL = dotenv.get("WEB_URL");
        adminEmail = dotenv.get("ADMIN_EMAIL");
        adminPassword = dotenv.get("ADMIN_PASSWORD");
        driver = new ChromeDriver();
    }
}
