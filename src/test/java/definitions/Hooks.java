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

    // Driver compartido por las clases de test
    public static WebDriver driver;

    // URL del Selenium Grid (puede ser "selenium-hub" si está en Docker)
    private static final String GRID_URL = "http://localhost:4444/wd/hub";

    @Before
    public void setUp() throws MalformedURLException {
        // Define el navegador desde línea de comandos: -Dbrowser=chrome o firefox
        String browser = System.getProperty("browser", "chrome").toLowerCase();

        if (browser.equals("firefox")) {
            FirefoxOptions ff = new FirefoxOptions();
            ff.setAcceptInsecureCerts(true);

            // 🚫 Headless (por defecto: no muestra interfaz)
            ff.addArguments("--headless");

            // 🖥️ Si quieres ver la interfaz de Firefox, comenta la línea anterior
            // ff.addArguments("--start-maximized");

            ff.addArguments("--width=1920", "--height=1080");
            driver = new RemoteWebDriver(new URL(GRID_URL), ff);

        } else {
            ChromeOptions ch = new ChromeOptions();
            ch.setAcceptInsecureCerts(true);

            // 🚫 Headless (por defecto: sin interfaz, consume menos recursos)
            ch.addArguments("--headless=new");

            // 🖥️ Si quieres ver la interfaz, comenta la línea anterior y descomenta esta:
            // ch.addArguments("--start-maximized");

            // 🔧 Flags recomendadas para evitar crash y reducir consumo en Docker
            ch.addArguments(
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu",
                    "--disable-extensions",
                    "--disable-popup-blocking",
                    "--window-size=1920,1080"
            );

            driver = new RemoteWebDriver(new URL(GRID_URL), ch);
        }

        // ⏱️ Tiempo de espera implícito (moderno y liviano)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    @After
    public void tearDown() {
        // Cierra el navegador y libera los recursos
        if (driver != null) {
            driver.quit();
        }
    }
}
