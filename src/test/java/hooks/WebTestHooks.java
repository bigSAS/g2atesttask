package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import steps.WebSteps;

public class WebTestHooks {

    private static final Logger logger = LogManager.getLogger(WebTestHooks.class);

    @After(value = "@Web", order = 998)
    public void closePlaywright(Scenario scenario) {
        if (scenario.isFailed()) {
            logger.warn("Failed scenario: {}", scenario.getName());
            var traceFile = System.getProperty("user.dir") + "/target/reports/" + normalizeScenarioName(scenario) + ".zip";
            logger.info("Closing Playwright with tracefile");
            WebSteps.closePlaywright(true, traceFile);
        } else {
            logger.info("Closing Playwright");
            WebSteps.closePlaywright();
        }
    }

    @After(value = "@Web", order = 999)
    public void takeScreenshotOnFail(Scenario scenario) {
        if (scenario.isFailed()) {
            var screenshot = WebSteps.getPage().screenshot();
            logger.info("Attaching screenshot to failed scenario");
            scenario.attach(screenshot, "image/png", "Screenshot");
        }
    }

    private String normalizeScenarioName(Scenario scenario) {
        var currentDateTimeReadable = java.time.LocalDateTime.now().toString().replace(":", "-");
        return currentDateTimeReadable + "." + scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");
    }
}
