package better.qa.e2e.product;

import better.qa.e2e.ProductTestBase;
import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ProductGetTest extends ProductTestBase {
    @Test
    @Description("ID: PRODUCT_GET_EXIST, Get an existing product")
    public void getAnExistingProduct() {
        driver.get(WEB_URL);
        WebElement firstProductOnHomepage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("app-overview div.container a.card"))
        );
        firstProductOnHomepage.click();

        waitUntilDataLoaded();

        WebElement detailElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("app-detail")
        ));
        List<WebElement> childElements = detailElement.findElements(By.xpath("./*"));
        assertEquals(childElements.size(), 3);
        assertEquals(childElements.get(0).getTagName(), "div");
        assertEquals(childElements.get(1).getTagName(), "hr");
        assertEquals(childElements.get(2).getTagName(), "div");
    }

    @Test
    @Description("ID: PRODUCT_GET_NOT_EXIST, Get a non-existing product")
    public void getANonExistingProduct() {
        driver.get(WEB_URL + "#/product/not-existing-product");
        waitUntilDataLoaded();
        WebElement detailElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("app-detail")
        ));
        List<WebElement> childElements = detailElement.findElements(By.xpath("./*"));

        assertTrue(
                childElements.size() != 3
                        || !childElements.get(0).getTagName().equals("div")
                        || !childElements.get(1).getTagName().equals("hr")
                        || !childElements.get(2).getTagName().equals("div")
        );
    }
}
