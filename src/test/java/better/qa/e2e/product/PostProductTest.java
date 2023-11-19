package better.qa.e2e.product;

import better.qa.e2e.ProductTestBase;
import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PostProductTest extends ProductTestBase {
    @Test
    @Description("ID: POST_NEW_PRODUCT, Create a new product")
    public void createNewProduct() {
        //Login as admin
        loginToAdminAccount();
        goToAddProductPage();

        //Fill in product details
        fillAddProductName();
        fillAddProductDataWithoutName();

        //Submit new product
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        submitButton.click();

        //Check if new product is displayed in products list
        driver.get(WEB_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[data-test].container")));
        WebElement productsContainer = driver.findElement(By.cssSelector("div[data-test].container"));
        wait.until(d -> !productsContainer.findElements(By.className("card")).isEmpty());

        WebElement newProduct = productsContainer.findElement(By.xpath("//h1[contains(text(),'Better Hammer')]"));
        assertTrue(newProduct.isDisplayed());

        //Log out
        signOut();
    }

    @Test
    @Description("ID: POST_NEW_PRODUCT_WITHOUT_NAME, Create a new product without name")
    public void createNewProductWithoutName() {
        //Login as admin
        loginToAdminAccount();
        goToAddProductPage();

        //Fill in product details
        fillAddProductDataWithoutName();

        //Submit new product
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        submitButton.click();

        //Check if error message is displayed
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Name is required')]")));
    }

    @Test
    @Description("ID: POST_NEW_PRODUCT_WITHOUT_NAME, Create a new product without name")
    public void createNewProductWithNegativePrice() {
        //Login as admin
        loginToAdminAccount();
        goToAddProductPage();

        //Fill in product details
        fillAddProductName();
        fillAddProductDataWithoutName();

        WebElement price = driver.findElement(By.cssSelector("input[formcontrolname='price']"));
        price.clear();
        price.sendKeys("-12345");

        //Submit new product
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        submitButton.click();

        //Check if error message is displayed
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Price should not be negative')]")));
    }

    private void goToAddProductPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'John Doe')]")));

        //Go to admin products page
        WebElement accountButton = driver.findElement(By.xpath("//a[contains(text(),'John Doe')]"));
        accountButton.click();
        WebElement adminProductsButton = driver.findElement(By.xpath("//a[contains(text(),'Products')]"));
        adminProductsButton.click();

        //Click on add new product button
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Add Product')]")));
        WebElement addNewProductButton = driver.findElement(By.xpath("//a[contains(text(),'Add Product')]"));
        addNewProductButton.click();
    }

    private void fillAddProductName() {
        WebElement name = driver.findElement(By.cssSelector("input[formcontrolname='name']"));
        name.sendKeys("Better Hammer");
    }

    private void fillAddProductDataWithoutName() {
        WebElement description = driver.findElement(By.cssSelector("textarea[formcontrolname='description']"));
        description.sendKeys("Better hammer for better nails");

        WebElement stock = driver.findElement(By.cssSelector("input[formcontrolname='stock']"));
        stock.sendKeys("1");

        WebElement price = driver.findElement(By.cssSelector("input[formcontrolname='price']"));
        price.sendKeys("12345");

        WebElement itemForRent = driver.findElement(By.cssSelector("input[formcontrolname='price']"));
        itemForRent.click();

        WebElement brand = driver.findElement(By.cssSelector("select[formcontrolname='brand_id']"));
        Select brandSelect = new Select(brand);
        wait.until(d -> brandSelect.getOptions().size() > 1);
        assertEquals(brandSelect.getOptions().size(), 3);
        assertTrue(brandSelect.getOptions().stream().map(WebElement::getText).toList().containsAll(List.of("", "Brand name 1", "Brand name 2")));
        brandSelect.getOptions().stream().filter(option -> option.getText().equals("Brand name 1")).findFirst().orElseThrow().click();

        WebElement category = driver.findElement(By.cssSelector("select[formcontrolname='category_id']"));
        Select categorySelect = new Select(category);
        wait.until(d -> categorySelect.getOptions().size() > 1);
        assertEquals(categorySelect.getOptions().size(), 13);
        assertTrue(categorySelect.getOptions().stream().map(WebElement::getText).toList().containsAll(List.of(
                "", "Hand Tools", "Power Tools", "Other", "Hammer", "Hand Saw", "Wrench", "Screwdriver", "Pliers", "Grinder", "Sander", "Saw", "Drill")
        ));
        categorySelect.getOptions().stream().filter(option -> option.getText().equals("Hand Tools")).findFirst().orElseThrow().click();

        WebElement image = driver.findElement(By.cssSelector("select[formcontrolname='product_image_id']"));
        Select imageSelect = new Select(image);
        wait.until(d -> imageSelect.getOptions().size() > 1);
        assertEquals(imageSelect.getOptions().size(), 30);
        assertTrue(imageSelect.getOptions().stream().map(WebElement::getText).toList().contains("Hammer"));
        imageSelect.getOptions().stream().filter(option -> option.getText().equals("Hammer")).findFirst().orElseThrow().click();
    }
}
