package pages;

import commons.Utils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutPage extends Utils {

    @FindBy(id = "first-name")protected WebElement txtNombre;
    @FindBy(id = "last-name")protected WebElement txtApellido;
    @FindBy(id = "postal-code")protected WebElement txtPostal;
    @FindBy(xpath = "//input[@value='CONTINUE']")protected WebElement btnContinuar;

    public CheckoutPage() {
        PageFactory.initElements(driver, this);
    }

    public void escribirNombre(String nombre){
        wait.until(ExpectedConditions.visibilityOf(txtNombre));
        txtNombre.sendKeys(nombre);
    }

    public void escribirApellido(String apellido){
        wait.until(ExpectedConditions.visibilityOf(txtApellido));
        txtApellido.sendKeys(apellido);
    }

    public void escribirCodigoPostal(String codigo){
        wait.until(ExpectedConditions.visibilityOf(txtPostal));
        txtPostal.sendKeys(codigo);
    }

    public void clickContinuar(){
        wait.until(ExpectedConditions.elementToBeClickable(btnContinuar));
        btnContinuar.click();
    }

}
