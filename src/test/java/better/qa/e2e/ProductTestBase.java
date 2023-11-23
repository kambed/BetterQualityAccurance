package better.qa.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.testng.Assert.assertTrue;

public class ProductTestBase extends TestBase {

    protected static final String EXISTING_PHARSE = "hammer";
    protected static final String NON_EXISTING_PHARSE = "dasdasd";

    protected WebElement searchedByPhrase(String phrase) {
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
