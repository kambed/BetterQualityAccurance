package better.qa.bdd;

import io.github.cdimascio.dotenv.Dotenv;
import org.awaitility.Awaitility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class BddTestBase {

    protected String WEB_URL;
    protected String adminEmail;
    protected String adminPassword;
    protected WebDriver driver;
    protected WebDriverWait wait;

    protected void setBaseUri() {
        Dotenv dotenv = Dotenv.load();
        WEB_URL = dotenv.get("WEB_URL");
        adminEmail = dotenv.get("ADMIN_EMAIL");
        adminPassword = dotenv.get("ADMIN_PASSWORD");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected void loginToAdminAccount() {
        //Go to login page
        driver.get(WEB_URL + "/#/auth/login");
        WebElement email = driver.findElement(By.cssSelector("input[formcontrolname='email']"));
        WebElement password = driver.findElement(By.cssSelector("input[formcontrolname='password']"));

        //Fill login form
        email.sendKeys("admin@practicesoftwaretesting.com");
        password.sendKeys("welcome01");

        //Submit login form
        WebElement loginButton = driver.findElement(By.cssSelector("input[value='Login']"));
        loginButton.click();

        //Wait for page to load
        wait.until(driver -> driver.findElement(By.xpath("//h1[contains(text(),'Sales over the years')]")));
        driver.get(WEB_URL);
    }

    protected void waitUntilDataLoaded() {
        Awaitility.with().pollDelay(2000, TimeUnit.MILLISECONDS).await().until(() -> true);
    }

    protected void tearDown() {
        driver.quit();
    }
}
