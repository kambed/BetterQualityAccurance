package better.qa.e2e.product;

import better.qa.e2e.TestBase;
import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ProductTest extends TestBase {

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

        WebElement containerElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.container[data-test='search_completed']")));

        int productCount = containerElement.findElements(By.className("card")).size();
        assertTrue(productCount >= 1);
        containerElement.findElements(By.className("card")).forEach(card -> {
            assertTrue(card.getText().toLowerCase().contains("hammer"));
        });
    }

    @Test
    @Description("ID: PRODUCTS_GET_RELATED_PRODUCTS_FOR_EXISTING_PRODUCT, Get related products for an existing product")
    public void getRelatedProductsForAnExistingProduct() {
        //Wait for API data to be loaded
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

    @Test
    @Description("ID: POST_NEW_PRODUCT, Create a new product")
    public void createNewProduct() {
        //Login as admin
        loginToAdminAccount();
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

        //Fill in product details
        WebElement name = driver.findElement(By.cssSelector("input[formcontrolname='name']"));
        name.sendKeys("Better Hammer");

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

        //Fill in product details
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
        brandSelect.getOptions().stream().filter(option -> option.getText().equals("Brand name 1")).findFirst().orElseThrow().click();

        WebElement category = driver.findElement(By.cssSelector("select[formcontrolname='category_id']"));
        Select categorySelect = new Select(category);
        wait.until(d -> categorySelect.getOptions().size() > 1);
        assertEquals(categorySelect.getOptions().size(), 13);
        categorySelect.getOptions().stream().filter(option -> option.getText().equals("Hand Tools")).findFirst().orElseThrow().click();

        WebElement image = driver.findElement(By.cssSelector("select[formcontrolname='product_image_id']"));
        Select imageSelect = new Select(image);
        wait.until(d -> imageSelect.getOptions().size() > 1);
        assertEquals(imageSelect.getOptions().size(), 30);
        assertTrue(imageSelect.getOptions().stream().map(WebElement::getText).toList().contains("Hammer"));
        imageSelect.getOptions().stream().filter(option -> option.getText().equals("Hammer")).findFirst().orElseThrow().click();

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

        //Fill in product details
        WebElement name = driver.findElement(By.cssSelector("input[formcontrolname='name']"));
        name.sendKeys("Better Hammer");

        WebElement description = driver.findElement(By.cssSelector("textarea[formcontrolname='description']"));
        description.sendKeys("Better hammer for better nails");

        WebElement stock = driver.findElement(By.cssSelector("input[formcontrolname='stock']"));
        stock.sendKeys("1");

        WebElement price = driver.findElement(By.cssSelector("input[formcontrolname='price']"));
        price.sendKeys("-12345");

        WebElement itemForRent = driver.findElement(By.cssSelector("input[formcontrolname='price']"));
        itemForRent.click();

        WebElement brand = driver.findElement(By.cssSelector("select[formcontrolname='brand_id']"));
        Select brandSelect = new Select(brand);
        wait.until(d -> brandSelect.getOptions().size() > 1);
        assertEquals(brandSelect.getOptions().size(), 3);
        brandSelect.getOptions().stream().filter(option -> option.getText().equals("Brand name 1")).findFirst().orElseThrow().click();

        WebElement category = driver.findElement(By.cssSelector("select[formcontrolname='category_id']"));
        Select categorySelect = new Select(category);
        wait.until(d -> categorySelect.getOptions().size() > 1);
        assertEquals(categorySelect.getOptions().size(), 13);
        categorySelect.getOptions().stream().filter(option -> option.getText().equals("Hand Tools")).findFirst().orElseThrow().click();

        WebElement image = driver.findElement(By.cssSelector("select[formcontrolname='product_image_id']"));
        Select imageSelect = new Select(image);
        wait.until(d -> imageSelect.getOptions().size() > 1);
        assertEquals(imageSelect.getOptions().size(), 30);
        assertTrue(imageSelect.getOptions().stream().map(WebElement::getText).toList().contains("Hammer"));
        imageSelect.getOptions().stream().filter(option -> option.getText().equals("Hammer")).findFirst().orElseThrow().click();

        //Submit new product
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        submitButton.click();

        //Check if error message is displayed
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Price should not be negative')]")));
    }
}
