package commons;

import definitions.hooks;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Utils extends hooks {

    public static WebDriverWait wait;
    public Utils() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void ingresarUrl(String url){
        driver.get("https://www.saucedemo.com/v1/index.html");
    }
}

