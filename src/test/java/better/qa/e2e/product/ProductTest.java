package better.qa.e2e.product;

import better.qa.e2e.TestBase;
import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

import static org.testng.Assert.*;

public class ProductTest extends TestBase {

    private static final String EXISTING_PHARSE = "hammer";
    private static final String NON_EXISTING_PHARSE = "dasdasd";
    private static final String UUID = "01HFHBPK5FAY43YKKMCM8JGVF3";

    @BeforeMethod
    public void setUp() {
        driver.get(WEB_URL);
        String pageTitle = driver.getTitle();
        assertEquals(pageTitle, NAME);
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

    @Test
    @Description("ID: PRODUCTS_PUT_CORRECT, Product update with correct data")
    public void editProductWithCorrectData() throws InterruptedException {
        prepareForProductEdit(UUID);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

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
    public void editProductWithInCorrectData() throws InterruptedException {
        String name = prepareForProductEdit(UUID);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

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

    private String prepareForProductEdit(String uuid) throws InterruptedException {
        loginToAdminAccount();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h1[data-test='page-title']")));
        driver.get(WEB_URL + "#/admin/products");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.table")));
        WebElement table = driver.findElement(By.cssSelector("table.table"));

        WebElement tbody = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(table, By.tagName("tbody")));
        List<WebElement> rows = tbody.findElements(By.tagName("tr"));
        WebElement row = rows.stream()
                .filter(r -> r.getText().contains(uuid))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such element"));
        List<WebElement> columns = row.findElements(By.tagName("td"));
        WebElement buttons = columns.get(4);
        String name = columns.get(1).getText();
        WebElement editButton = buttons.findElement(By.cssSelector("a.btn.btn-sm.btn-primary"));
        editButton.click();

        //wait until data is loaded
        Thread.sleep(2000);
        signOut();

        return name;
    }

    private static ExpectedCondition<Boolean> isToastPresent(By locator) {
        return driver -> !driver.findElements(locator).isEmpty();
    }

    private WebElement searchedByPhrase(String phrase) {
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
}
