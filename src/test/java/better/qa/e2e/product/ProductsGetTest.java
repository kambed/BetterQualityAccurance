package better.qa.e2e.product;

import better.qa.e2e.ProductTestBase;
import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import static org.testng.Assert.*;

public class ProductsGetTest extends ProductTestBase {
    @BeforeMethod
    public void setUp() {
        driver.get(WEB_URL);
        String pageTitle = driver.getTitle();
        assertEquals(pageTitle, NAME);
    }

    @Test
    @Description("ID: PRODUCTS_GET_ALL, Get all products")
    public void getAllProducts() {
        WebElement containerElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("app-overview div.container"))
        );

        int productCount = containerElement.findElements(By.cssSelector("a.card")).size();
        assertTrue(productCount >= 1);
    }

    @Test
    @Description("ID: PRODUCTS_GET_SEARCH_FOUND, Search for products using an existing phrase")
    public void searchForProductsUsingAnExistingPhrase() {
        WebElement containerElement = searchedByPhrase(EXISTING_PHARSE);

        int productCount = containerElement.findElements(By.className("card")).size();
        assertTrue(productCount >= 1);
        containerElement.findElements(By.className("card")).forEach(card -> assertTrue(card.getText().toLowerCase().contains(EXISTING_PHARSE)));
    }

    @Test
    @Description("ID: PRODUCTS_GET_SEARCH_NOT_FOUND, Search for products by a non-existent phrase")
    public void searchForProductsUsingANonExistingPhrase() {
        WebElement containerElement = searchedByPhrase(NON_EXISTING_PHARSE);

        int productCount = containerElement.findElements(By.className("card")).size();
        assertEquals(productCount, 0);

        List<WebElement> childDivs = containerElement.findElements(By.cssSelector("div[data-test='no-results']"));
        assertEquals(childDivs.size(), 1);
        assertEquals(childDivs.get(0).getText(), "No results found.");
    }
}
