package definitions;

import commons.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Hooks {

    // Driver compartido entre los steps
    public static WebDriver driver;

    private static String resolveGridUrl() {
        // 1) Propiedad del sistema: -DseleniumGridUrl=http://...
        String fromProp = System.getProperty("seleniumGridUrl");
        if (fromProp != null && !fromProp.isBlank()) {
            return fromProp;
        }

        // 2) Variable de entorno: SELENIUM_GRID_URL
        String fromEnv = System.getenv("SELENIUM_GRID_URL");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }

        // 3) Si se está ejecutando dentro de un contenedor (/.dockerenv existe),
        try {
            if (new java.io.File("/.dockerenv").exists()) {
                return "http://selenium-hub:4444/wd/hub";
            }
        } catch (Exception ignored) {
            // Si falla sigue al fallback local
        }

        // 4) Fallback local cuando se corre desde la máquina host
        return "http://localhost:4444/wd/hub";
    }

    @Before
    public void setUp() throws MalformedURLException {
        // URL del Grid (puede venir de -DseleniumGridUrl, env, Docker o fallback)
        String gridUrl = resolveGridUrl();
        System.out.println("[Hooks] Selenium Grid URL: " + gridUrl);

        // Browser por propiedad del sistema: -Dbrowser=chrome / -Dbrowser=firefox (default: chrome)
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        System.out.println("[Hooks] Browser: " + browser);

        // Configuración para Firefox
        if ("firefox".equals(browser)) {
            FirefoxOptions ff = new FirefoxOptions();

            // Acepta certificados inseguros si el entorno lo requiere
            ff.setAcceptInsecureCerts(true);

            // Ejecuta en modo headless con tamaño de ventana definido
            ff.addArguments("-headless", "--width=1920", "--height=1080");

            // Crea RemoteWebDriver apuntando al Grid
            driver = new RemoteWebDriver(new URL(gridUrl), ff);

        } else {
            // Configuración para Chrome
            ChromeOptions ch = new ChromeOptions();

            // Aceptar certificados inseguros si el entorno lo requiere
            ch.setAcceptInsecureCerts(true);

            // Desactiva la password manager y popup de guardar/cambiar contraseña
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            ch.setExperimentalOption("prefs", prefs);

            // Flags de Chrome para ejecución en entorno de Grid / Docker
            ch.addArguments(
                    "--headless=new",          // Modo headless moderno
                    "--no-sandbox",            // Recomendado en contenedores
                    "--disable-dev-shm-usage", // Evita problemas de /dev/shm pequeño
                    "--disable-gpu",           // No se usa GPU en contenedor
                    "--disable-extensions",    // Evita extensiones inesperadas
                    "--disable-popup-blocking",// Control de popups del propio navegador
                    "--window-size=1920,1080"  // Tamaño de ventana consistente
            );

            // Crear RemoteWebDriver apuntando al Grid
            driver = new RemoteWebDriver(new URL(gridUrl), ch);
        }

        // Timeout implícito genérico para encontrar elementos
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            // Screenshot solo cuando falla (delegado al helper)
            ScreenshotUtil.captureOnFailure(driver, scenario);
        } finally {
            // Cerrar el navegador al final de cada escenario
            if (driver != null) {
                driver.quit();
                System.out.println("[Hooks] WebDriver cerrado correctamente.");
            }
        }
    }
}
