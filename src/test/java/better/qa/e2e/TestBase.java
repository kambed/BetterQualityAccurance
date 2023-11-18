package better.qa.e2e;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

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

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

    protected void loginAsAdmin() {
        driver.get(WEB_URL + "#/auth/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[formcontrolname='email']")));
        emailInput.sendKeys(adminEmail);

        WebElement passwordInput = driver.findElement(By.cssSelector("input[formcontrolname='password']"));
        passwordInput.sendKeys(adminPassword);

        WebElement loginButton = driver.findElement(By.cssSelector("input[data-test='login-submit']"));
        loginButton.click();
    }
}
