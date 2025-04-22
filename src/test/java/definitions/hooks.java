package definitions;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class hooks {

    public static WebDriver driver;

    private static final String GRID_URL = "http://localhost:4444/wd/hub";

    @Before
    public void setUp() throws MalformedURLException {
        String browser = System.getProperty("browser", "chrome");

        Capabilities capabilities;

        if (browser.equalsIgnoreCase("firefox")) {
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.addArguments("--start-maximized");
            firefoxOptions.addArguments("--headless");
            capabilities = firefoxOptions;
        } else {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--start-maximized");
            chromeOptions.addArguments("--headless");
            capabilities = chromeOptions;
        }

        driver = new RemoteWebDriver(new URL(GRID_URL), capabilities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterStep
    public void attachScreenshotOnFailure(Scenario scenario) {
        if (scenario.isFailed() && driver != null) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure - Step: " + scenario.getName(), new ByteArrayInputStream(screenshot));
        }
    }
}

