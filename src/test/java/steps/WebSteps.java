package steps;

import com.microsoft.playwright.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.PropertiesReader;

import java.nio.file.Paths;

public abstract class WebSteps {
    private static final Logger logger = LogManager.getLogger(WebSteps.class);
    private static Playwright playwright;
    private static Browser browser;

    private static BrowserContext context;
    private static Page page;
    private static final PropertiesReader props = new PropertiesReader("config.properties");

    public PropertiesReader getProps() {
        return props;
    }

    public static Page getPage() {
        if (page == null) {
            playwright = Playwright.create();
            var browserType = props.getProperty("browser.type");
            var headed = props.getProperty("browser.headed").equals("true");
            var sloMo = props.getProperty("browser.slomo.ms");
            logger.info("Browser type: {}", browserType);
            logger.info("Headed: {}", headed);
            logger.info("SloMo: {}", sloMo);
            var launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setHeadless(!headed);
            launchOptions.setSlowMo(Integer.parseInt(sloMo));
            switch (browserType) {
                case "chromium":
                    browser = playwright.chromium().launch(launchOptions);
                    break;
                case "firefox":
                    browser = playwright.firefox().launch(launchOptions);
                    break;
                case "webkit":
                    browser = playwright.webkit().launch(launchOptions);
                    break;
                default:
                    throw new RuntimeException("Unsupported browser type: " + browserType);
            }
            context = browser.newContext();
            // set default timeout
            var timeout = Integer.parseInt(props.getProperty("browser.context.default.timeout.ms"));
            context.setDefaultTimeout(timeout);
            logger.info("Default timeout: {} ms", timeout);
            // Start tracing before creating / navigating a page.
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
            page = context.newPage();
        }
        return page;
    }

    public static void closePlaywright() {
        closePlaywright(false, "");
    }

    public static void closePlaywright(boolean saveTrace, String traceFile) {
        logger.info("Closing Playwright");
        logger.info("Save trace: {}", saveTrace);
        if (saveTrace) {
            logger.info("Trace file: {}", traceFile);
        }
        if (page != null) {
            page.close();
            page = null;
        }
        if (context != null) {
            if (saveTrace) {
                var tracePath = Paths.get(traceFile);
                logger.info("Tracing data: {}", tracePath.toAbsolutePath());
                context.tracing().stop(
                        new Tracing.StopOptions().setPath(tracePath)
                );
            }
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}
