package better.qa.e2e.product;

import better.qa.e2e.TestE2ETest;
import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ProductE2ETest extends TestE2ETest {

    @BeforeMethod
    public void setUp() {
        driver.get(WEB_URL);
        String pageTitle = driver.getTitle();
        assertEquals(pageTitle, NAME);
    }

    @Test
    @Description("ID: PRODUCTS_GET_SEARCH_FOUND, Search for products using an existing phrase")
    public void searchForProductsUsingAnExistingPhrase() {
        WebElement inputField = driver.findElement(By.cssSelector("[formcontrolname='query']"));
        inputField.sendKeys("hammer");

        WebElement searchButton = driver.findElement(By.cssSelector("[data-test='search-submit']"));
        searchButton.click();

        WebElement h3Element = driver.findElement(By.cssSelector("h3"));
        assertTrue(h3Element.getText().contains("hammer"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 10 seconds timeout
        WebElement containerElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.container[data-test='search_completed']")));

        int productCount = containerElement.findElements(By.className("card")).size();
        assertTrue(productCount >= 1);
        containerElement.findElements(By.className("card")).forEach(card -> {
            assertTrue(card.getText().toLowerCase().contains("hammer"));
        });
    }


    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
