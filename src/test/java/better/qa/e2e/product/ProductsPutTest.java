package better.qa.e2e.product;

import better.qa.e2e.ProductTestBase;
import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class ProductsPutTest extends ProductTestBase {
    @BeforeMethod
    public void setUp() {
        driver.get(WEB_URL);
        String pageTitle = driver.getTitle();
        assertEquals(pageTitle, NAME);
    }

    @Test
    @Description("ID: PRODUCTS_PUT_CORRECT, Product update with correct data")
    public void editProductWithCorrectData() {
        prepareForProductEdit();

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='name']")));
        nameInput.clear();
        nameInput.sendKeys("Updated product name");

        WebElement descriptionInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='description']")));
        descriptionInput.clear();
        descriptionInput.sendKeys("Updated product description");

        WebElement stockInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='stock']")));
        stockInput.clear();
        stockInput.sendKeys("100");

        WebElement priceInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='price']")));
        priceInput.clear();
        priceInput.sendKeys("1000");

        WebElement saveButton = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        saveButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
        WebElement successAlert = driver.findElement(By.className("alert-success"));
        assertTrue(successAlert.getText().contains("Product saved!"));

        By toastLocator = By.xpath("/html/body/app-root/app-toasts");
        boolean isToastVisible = wait.until(isToastPresent(toastLocator));
        assertTrue(isToastVisible);

        WebElement container = searchedByPhrase("Updated product name");
        int productCount = container.findElements(By.className("card")).size();
        assertEquals(productCount, 1);
        assertTrue(container.findElement(By.className("card")).getText().contains("Updated product name"));
    }

    @Test
    @Description("ID: PRODUCTS_PUT_INCORRECT, Product update with incorrect data")
    public void editProductWithInCorrectData() {
        String name = prepareForProductEdit();

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='name']")));
        prepareInput(nameInput);
        checkToastMessage("/html/body/app-root/div/app-products-add-edit/div/form/div[2]/div[1]/div[2]/div[2]/div", "Name is required");

        WebElement descriptionInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='description']")));
        descriptionInput.clear();
        descriptionInput.sendKeys(Keys.ENTER);
        descriptionInput.sendKeys(Keys.BACK_SPACE);
        checkToastMessage("/html/body/app-root/div/app-products-add-edit/div/form/div[2]/div[1]/div[3]/div[2]/div", "Description is required");

        WebElement stockInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='stock']")));
        prepareInput(stockInput);
        WebElement isPresent = driver.findElement(By.xpath("/html/body/app-root/div/app-products-add-edit/div/form/div[2]/div[1]/div[4]/div[2]"));
        assertTrue(isPresent.isDisplayed());

        WebElement priceInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='price']")));
        prepareInput(priceInput);
        checkToastMessage("/html/body/app-root/div/app-products-add-edit/div/form/div[2]/div[1]/div[5]/div[2]/div", "Price is required");

        WebElement container = searchedByPhrase(name);
        int productCount = container.findElements(By.className("card")).size();
        assertEquals(productCount, 1);
        assertTrue(container.findElement(By.className("card")).getText().contains(name));
    }

    private void checkToastMessage(String xPath, String message) {
        try {
            WebElement element = driver.findElement(By.xpath(xPath));
            assertEquals(element.getText(), message);
        } catch (Exception e) {
            fail();
        }
    }

    private void prepareInput(WebElement input) {
        input.clear();
        input.sendKeys("d");
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(Keys.ENTER);
    }

    private String prepareForProductEdit() {
        loginToAdminAccount();
        driver.get(WEB_URL + "#/admin/products");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.table")));
        WebElement table = driver.findElement(By.cssSelector("table.table"));

        WebElement tbody = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(table, By.tagName("tbody")));
        List<WebElement> rows = tbody.findElements(By.tagName("tr"));
        WebElement row = rows.get(0);
        List<WebElement> columns = row.findElements(By.tagName("td"));
        WebElement buttons = columns.get(4);
        String name = columns.get(1).getText();
        WebElement editButton = buttons.findElement(By.cssSelector("a.btn.btn-sm.btn-primary"));
        editButton.click();

        waitUntilDataLoaded();

        return name;
    }

    private static ExpectedCondition<Boolean> isToastPresent(By locator) {
        return driver -> {
            assert driver != null;
            return !driver.findElements(locator).isEmpty();
        };
    }
}
