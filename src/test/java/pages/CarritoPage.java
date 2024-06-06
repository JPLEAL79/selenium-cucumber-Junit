package pages;

import commons.Utils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CarritoPage extends Utils {

    @FindBy(xpath = "//a[@class='btn_action checkout_button']")protected WebElement btnCheckout;

    public CarritoPage() {
        PageFactory.initElements(driver, this);
    }

    public void clickCheckout(){
        wait.until(ExpectedConditions.elementToBeClickable(btnCheckout));
        btnCheckout.click();
    }
}
