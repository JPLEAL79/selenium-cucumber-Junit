package definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

public class hooks {


    public static WebDriver driver;

    @Before
    public static void setUp(){
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--disable-notifications");
        opts.addArguments("--start-maximized");
        driver = new ChromeDriver(opts);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @After
    public static void tearDown(){
        driver.manage().deleteAllCookies();
        driver.close();
    }
}
