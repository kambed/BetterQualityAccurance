package better.qa.e2e.product;

import better.qa.e2e.ProductTestBase;
import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotEquals;

public class ProductsDeleteTest extends ProductTestBase {
    @Test
    @Description("ID: PRODUCT_DELETE_EXISTS, Delete an existing product")
    public void deleteAnExistingProduct() {
        loginToAdminAccount();
        driver.get(WEB_URL + "#/admin/products");

        WebElement firstRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table > tbody > tr")));
        String id = firstRow.findElement(By.cssSelector("td:nth-child(1)")).getText();
        String name = firstRow.findElement(By.cssSelector("td:nth-child(2)")).getText();

        WebElement deleteButton = firstRow.findElement(By.cssSelector("button.btn-danger"));
        deleteButton.click();

        firstRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table > tbody > tr")));
        String newId = firstRow.findElement(By.cssSelector("td:nth-child(1)")).getText();

        assertNotEquals(id, newId);

        WebElement productsContainer = searchedByPhrase(name);
        List<WebElement> productsTitles = productsContainer.findElements(By.cssSelector("a.card > div.card-body > h5"));
        productsTitles.forEach(title -> assertNotEquals(title.getText(), name));
    }
}
