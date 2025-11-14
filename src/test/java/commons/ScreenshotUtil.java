package commons;

import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {

    // Screenshot solo cuando el escenario falla
    public static void captureOnFailure(WebDriver driver, Scenario scenario) {
        if (driver == null) {
            return;
        }

        // Solo si el escenario falló y el driver soporta screenshots
        if (!scenario.isFailed() || !(driver instanceof TakesScreenshot)) {
            return;
        }

        try {
            System.out.println("[ScreenshotUtil] Scenario failed, taking screenshot: " + scenario.getName());

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            // Nombre de archivo seguro + timestamp
            String safeName = scenario.getName()
                    .replaceAll("[^a-zA-Z0-9-_\\.]", "_");

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String fileName = "target/screenshots/" + safeName + "_" + timestamp + ".png";

            File destFile = new File(fileName);
            destFile.getParentFile().mkdirs();
            Files.write(destFile.toPath(), screenshot);

            // Adjunta a Allure
            Allure.addAttachment(
                    "Screenshot - " + scenario.getName(),
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    "png"
            );
        } catch (Exception e) {
            System.out.println("[ScreenshotUtil] Could not capture screenshot: " + e.getMessage());
        }
    }
}
