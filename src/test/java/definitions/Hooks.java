package definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class Hooks {

    public static WebDriver driver;

    // Resolver URL del Grid sin hardcodear localhost
    private static String resolveGridUrl() {
        // Primero lee lo que se pasa por -DseleniumGridUrl
        String fromProp = System.getProperty("seleniumGridUrl",
                System.getProperty("selenium.grid.url",
                        System.getProperty("grid.url", null)));

        if (fromProp != null && !fromProp.isBlank()) return fromProp;

        String fromEnv = System.getenv("SELENIUM_GRID_URL");
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;

        // Detecta si está corriendo dentro de Docker (Jenkins)
        String inDocker = System.getenv("RUNNING_IN_DOCKER");

        // Si no está en Docker → usa localhost
        if (inDocker == null) {
            return "http://localhost:4444/wd/hub";
        }

        // Fallback para ejecución en Jenkins/Docker
        return "http://selenium-hub:4444/wd/hub";
    }

    @Before
    public void setUp() throws MalformedURLException {
        String gridUrl = resolveGridUrl();
        System.out.println("[Hooks] Selenium Grid URL: " + gridUrl);

        String browser = System.getProperty("browser", "chrome").toLowerCase();
        System.out.println("[Hooks] Browser: " + browser);

        if ("firefox".equals(browser)) {
            FirefoxOptions ff = new FirefoxOptions();
            ff.setAcceptInsecureCerts(true);
            ff.addArguments("-headless", "--width=1920", "--height=1080");
            driver = new RemoteWebDriver(new URL(gridUrl), ff);
        } else {
            ChromeOptions ch = new ChromeOptions();
            ch.setAcceptInsecureCerts(true);
            ch.addArguments(
                    "--headless=new",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu",
                    "--disable-extensions",
                    "--disable-popup-blocking",
                    "--window-size=1920,1080"
            );
            driver = new RemoteWebDriver(new URL(gridUrl), ch);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
