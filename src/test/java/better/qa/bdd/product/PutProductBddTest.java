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

import java.util.List;
import java.util.NoSuchElementException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PutProductBddTest extends BddTestBase {

    @Before
    public void setUp() {
        super.setBaseUri();
    }

    @Given("User is logged in as admin and is on the products page")
    public void userIsLoggedAsAdminAndIsOnAddProductPage() {
        loginToAdminAccount();
        goToProductPage();
    }

    @When("User selects a product with the name {string}")
    public void userSelectsAProductWithTheName(String productName) {
        selectProductData(productName);
    }

    @And("User edits the product name to {string}")
    public void userEditsTheProductNameTo(String newProductName) {
        fillProductDataInput("name", newProductName);
    }

    @And("User edits the product description to {string}")
    public void userEditsTheProductDescriptionTo(String description) {
        fillProductDataTextArea("description", description);
    }

    @And("User saves the changes")
    public void userSavesTheChanges() {
        WebElement saveButton = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        saveButton.click();
    }

    @Then("Success message {string} should be displayed")
    public void successMessageShouldBeDisplayed(String message) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'" + message + "')]")
                )
        );
    }

    @And("The product's name should be {string}")
    public void theProductSNameShouldBe(String updatedProductName) {
        WebElement container = searchedByPhrase(updatedProductName);
        int productCount = container.findElements(By.className("card")).size();
        assertEquals(productCount, 1);
        assertTrue(container.findElement(By.className("card")).getText().contains(updatedProductName));
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    private void selectProductData(String productName) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.table")));
        WebElement table = driver.findElement(By.cssSelector("table.table"));

        WebElement tbody = wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(table, By.tagName("tbody")));
        List<WebElement> rows = tbody.findElements(By.tagName("tr"));

        WebElement row = rows.stream().filter(r -> {
            List<WebElement> columns = r.findElements(By.tagName("td"));
            return columns.get(1).getText().equals(productName);
        }).findFirst().orElseThrow(() ->
                new NoSuchElementException("No such element with name %s. Make sure that the database data has not changed".formatted(productName))
        );
        List<WebElement> columns = row.findElements(By.tagName("td"));
        WebElement buttons = columns.get(4);
        WebElement editButton = buttons.findElement(By.cssSelector("a.btn.btn-sm.btn-primary"));
        editButton.click();
        waitUntilDataLoaded();
    }
}