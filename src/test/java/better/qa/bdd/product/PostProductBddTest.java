package better.qa.bdd.product;

import better.qa.bdd.BddTestBase;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class PostProductBddTest extends BddTestBase {

    @Before
    public void setUp() {
        super.setBaseUri();
    }

    @Given("User is logged as admin and is on add product page")
    public void userIsLoggedAsAdminAndIsOnAddProductPage() {
        //Login as admin
        loginToAdminAccount();
        goToAddProductPage();
    }

    @When("User fills product name with {string}")
    public void userFillsProductNameWith(String productName) {
        fillProductDataInput("name", productName);
    }

    @And("User fills product description with {string}")
    public void userFillsProductDescriptionWith(String productDescription) {
        WebElement name = driver.findElement(By.cssSelector("textarea[formcontrolname='description']"));
        name.sendKeys(productDescription);
    }

    @And("User fills product stock with {string}")
    public void userFillsProductStockWith(String productStock) {
        fillProductDataInput("stock", productStock);
    }

    @And("User fills product price with {string}")
    public void userFillsProductPriceWith(String productPrice) {
        fillProductDataInput("price", productPrice);
    }

    @And("User selects product brand {string}")
    public void userSelectsProductBrand(String brandName) {
        selectProductData("brand_id", brandName);
    }

    @And("User selects product category {string}")
    public void userSelectsProductCategory(String categoryName) {
        selectProductData("category_id", categoryName);
    }

    @And("User selects product image {string}")
    public void userSelectsProductImage(String imageName) {
        selectProductData("product_image_id", imageName);
    }

    @And("User submits new product")
    public void userSubmitsNewProduct() {
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
        submitButton.click();
    }

    @Then("Error message {string} is displayed")
    public void errorMessageIsDisplayed(String message) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'" + message + "')]")
                )
        );
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    private void selectProductData(String field, String value) {
        WebElement name = driver.findElement(By.cssSelector("select[formcontrolname='" + field + "']"));
        Select select = new Select(name);
        wait.until(d -> select.getOptions().size() > 1);
        select.getOptions()
                .stream()
                .filter(option -> option.getText().equals(value))
                .findFirst()
                .orElseThrow()
                .click();
    }
}
