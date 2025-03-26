package definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class hooks {


    public static WebDriver driver;

    private static final String GRID_URL = "http://localhost:4444/wd/hub"; // Usar localhost en lugar de IP del Hub

    @Before
    public void setUp() throws MalformedURLException {
        String browser = System.getProperty("browser", "chrome"); // Permite definir el navegador desde la línea de comandos

        Capabilities capabilities;

        // Definir opciones para Chrome y Firefox
        if (browser.equalsIgnoreCase("firefox")) {
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            capabilities = firefoxOptions;  // Usamos FirefoxOptions que implementa Capabilities
        } else {
            ChromeOptions chromeOptions = new ChromeOptions();
            capabilities = chromeOptions;  // Usamos ChromeOptions que implementa Capabilities
        }

        // Crear el WebDriver remoto usando la URL del Hub
        driver = new RemoteWebDriver(new URL(GRID_URL), capabilities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS); // Establecer el tiempo de espera implícito
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // Cerrar el navegador
        }
    }
}