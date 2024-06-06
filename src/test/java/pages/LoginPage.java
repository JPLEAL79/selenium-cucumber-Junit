package pages;

import commons.Utils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends Utils {


    @FindBy(id = "user-name")protected WebElement txtUsuario;
    @FindBy(id = "password")protected WebElement txtClave;
    @FindBy(id = "login-button")protected WebElement btnLogin;
    @FindBy(xpath = "//h3[@data-test='error']")protected WebElement lblmsjerror;

    public LoginPage() {
        PageFactory.initElements(driver, this);
    }

    public void escribirUsuario(String user){
        wait.until(ExpectedConditions.visibilityOf(txtUsuario));
        txtUsuario.sendKeys(user);
    }

    public void escribirClave(String pass){
        wait.until(ExpectedConditions.visibilityOf(txtClave));
        txtClave.sendKeys(pass);
    }

    public void clickLogin(){
        wait.until(ExpectedConditions.elementToBeClickable(btnLogin));
        btnLogin.click();
    }

    public void validarMensajeError(String msj){
        wait.until(ExpectedConditions.visibilityOf(lblmsjerror));
        Assertions.assertEquals(msj,lblmsjerror.getText());
    }
}
