package better.qa.e2e;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

public class TestBase {

    protected static final String NAME = "Practice Software Testing - Toolshop - v5.0";
    protected String WEB_URL;
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeClass
    protected void setBaseUri() {
        Dotenv dotenv = Dotenv.load();
        WEB_URL = dotenv.get("WEB_URL");
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

    protected void signOut() {
        //Go to home page
        driver.get(WEB_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'John Doe')]")));

        //Sign out
        WebElement accountButton = driver.findElement(By.xpath("//a[contains(text(),'John Doe')]"));
        accountButton.click();
        WebElement signOutButton = driver.findElement(By.xpath("//a[contains(text(),'Sign out')]"));
        signOutButton.click();

        driver.get(WEB_URL);
    }

    @AfterClass
    protected void tearDown() {
        driver.quit();
    }
}
