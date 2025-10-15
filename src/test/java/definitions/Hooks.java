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

    //  Resolver URL del Selenium Grid (Docker o local)
    private static String resolveGridUrl() {
        // Busca primero una propiedad del sistema (-DseleniumGridUrl)
        String fromProp = System.getProperty("seleniumGridUrl");
        if (fromProp != null && !fromProp.isBlank()) return fromProp;

        // Luego revisa la variable de entorno (útil en Jenkins o Docker)
        String fromEnv = System.getenv("SELENIUM_GRID_URL");
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;

        // Detecta si está en Docker por hostname del Hub (más confiable que RUNNING_IN_DOCKER)
        try {
            if (new java.io.File("/.dockerenv").exists()) {
                return "http://selenium-hub:4444/wd/hub";
            }
        } catch (Exception ignored) {}

        // Fallback local (cuando se ejecuta fuera de contenedor)
        return "http://localhost:4444/wd/hub";
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
        if (driver != null) {
            driver.quit();
            System.out.println("[Hooks] WebDriver cerrado correctamente.");
        }
    }
}
