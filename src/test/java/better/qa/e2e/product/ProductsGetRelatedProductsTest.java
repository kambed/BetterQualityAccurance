package better.qa.e2e.product;

import better.qa.e2e.ProductTestBase;
import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ProductsGetRelatedProductsTest extends ProductTestBase {
    @Test
    @Description("ID: PRODUCTS_GET_RELATED_PRODUCTS_FOR_EXISTING_PRODUCT, Get related products for an existing product")
    public void getRelatedProductsForAnExistingProduct() {
        //Wait for API data to be loaded
        driver.get(WEB_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[data-test].container")));
        WebElement productsContainer = driver.findElement(By.cssSelector("div[data-test].container"));
        wait.until(d -> !productsContainer.findElements(By.className("card")).isEmpty());

        //Go to combination pliers product page
        WebElement combinationPliers = driver.findElement(By.xpath("//h5[contains(text(),'Combination Pliers')]"));
        combinationPliers.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Combination Pliers')]")));

        //Find related products section by title
        WebElement relatedProductsTitle = driver.findElement(By.xpath("//h1[contains(text(),'Related products')]"));
        assertTrue(relatedProductsTitle.isDisplayed());
        WebElement parentElement = relatedProductsTitle.findElement(By.xpath("./.."));

        //Check if related products are displayed as expected
        List<String> expectedRelatedProducts = List.of("Pliers", "Bolt Cutters", "Long Nose Pliers", "Slip Joint Pliers");
        List<WebElement> relatedProducts = parentElement.findElement(By.className("container")).findElements(By.className("card"));
        assertTrue(relatedProducts.size() >= 4);

        List<String> relatedProductsText = relatedProducts.stream().map(card -> card.findElement(By.className("card-title")).getText()).toList();
        relatedProductsText.forEach(name -> assertTrue(expectedRelatedProducts.contains(name)));
        assertEquals(relatedProductsText.stream().distinct().toList(), relatedProductsText);

        //Find pliers product card in related products section and check if it is displayed as expected
        WebElement pliers = relatedProducts.stream().filter(card -> card.findElement(By.className("card-title")).getText().equals("Pliers")).findFirst().orElseThrow();
        Assert.assertTrue(pliers.findElement(By.className("card-img-top")).getAttribute("src").contains("assets/img/products/pliers02.jpeg"));
        Assert.assertEquals(pliers.findElement(By.className("card-title")).getText(), "Pliers");

        //Click on details button and check if pliers product details page is displayed
        WebElement pliersDetailsButton = pliers.findElement(By.tagName("a"));
        pliersDetailsButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Pliers')]")));

        WebElement pliersDetails = driver.findElement(By.xpath("//h1[contains(text(),'Pliers')]"));
        assertTrue(pliersDetails.isDisplayed());
    }

    @Test
    @Description("ID: PRODUCTS_GET_RELATED_PRODUCTS_FOR_NOT_EXISTING_PRODUCT, Get related products for a non-existing product")
    public void getRelatedProductsForANotExistingProduct() {
        //Go to not existing product page and wait for API data to be loaded
        driver.get(WEB_URL + "#/product/not-existing-product");

        //Find related products section by title
        WebElement relatedProductsTitle = driver.findElement(By.xpath("//h1[contains(text(),'Related products')]"));
        assertTrue(relatedProductsTitle.isDisplayed());
        WebElement parentElement = relatedProductsTitle.findElement(By.xpath("./.."));

        //Check if related products are not displayed as expected
        List<WebElement> relatedProducts = parentElement.findElement(By.className("container")).findElements(By.className("card"));
        assertEquals(relatedProducts.size(), 0);
    }
}
