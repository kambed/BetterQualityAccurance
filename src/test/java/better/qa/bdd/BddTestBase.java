package better.qa.bdd;

import io.github.cdimascio.dotenv.Dotenv;
import org.awaitility.Awaitility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

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

    protected void goToProductPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'John Doe')]")));

        //Go to admin products page
        WebElement accountButton = driver.findElement(By.xpath("//a[contains(text(),'John Doe')]"));
        accountButton.click();
        WebElement adminProductsButton = driver.findElement(By.xpath("//a[contains(text(),'Products')]"));
        adminProductsButton.click();
    }

    protected void goToAddProductPage() {
        goToProductPage();

        //Click on add new product button
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Add Product')]")));
        WebElement addNewProductButton = driver.findElement(By.xpath("//a[contains(text(),'Add Product')]"));
        addNewProductButton.click();
    }

    protected void waitUntilDataLoaded() {
        Awaitility.with().pollDelay(2000, TimeUnit.MILLISECONDS).await().until(() -> true);
    }

    protected void fillProductDataInput(String field, String value) {
        WebElement name = driver.findElement(By.cssSelector("input[formcontrolname='" + field + "']"));
        name.clear();
        name.sendKeys(value);
    }

    protected void fillProductDataTextArea(String field, String value) {
        WebElement name = driver.findElement(By.cssSelector("textarea[formcontrolname='" + field + "']"));
        name.clear();
        name.sendKeys(value);
    }

    protected WebElement searchedByPhrase(String phrase) {
        driver.get(WEB_URL);
        WebElement inputField = driver.findElement(By.cssSelector("[formcontrolname='query']"));
        inputField.sendKeys(phrase);

        WebElement searchButton = driver.findElement(By.cssSelector("[data-test='search-submit']"));
        searchButton.click();

        WebElement h3Element = driver.findElement(By.cssSelector("h3"));
        assertTrue(h3Element.getText().contains(phrase));

        return wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("div.container[data-test='search_completed']")));
    }

    protected void tearDown() {
        driver.quit();
    }
}
