package better.qa.bdd.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(tags = "",
        features = {"src/test/resources/features/PostProduct.feature"},
        glue = {"better.qa.bdd"},
        plugin = {}
)
public class PostProductCucumberRunnerTests extends AbstractTestNGCucumberTests {
}