package definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class hooks {


    public static WebDriver driver;

    @Before
    public static void setUp() throws MalformedURLException {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--disable-notifications");
        opts.addArguments("--start-maximized");
        driver = new RemoteWebDriver(new URL("http://3.141.41.156:4444"),opts);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @After
    public static void tearDown(){
        driver.manage().deleteAllCookies();
        driver.close();
    }
}
