package pages;

import commons.Utils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductsPage extends Utils {


    @FindBy(xpath = "//*[@id='header_container']/div[2]")protected WebElement lblTitulo;
    @FindBy(xpath = "//*[@id='shopping_cart_container']/a")protected WebElement btnCarrito;

    public ProductsPage() {
        PageFactory.initElements(driver, this);
    }

    public void validarTitulo(){
        wait.until(ExpectedConditions.visibilityOf(lblTitulo));
        Assertions.assertTrue(lblTitulo.isDisplayed());
    }

    public void seleccionarProducto(String valor){
        WebElement producto = driver.findElement(By.xpath("//div[text()='"+valor+"']/following::button"));
        wait.until(ExpectedConditions.elementToBeClickable(producto));
        producto.click();
    }

    public void clickCarrito(){
        wait.until(ExpectedConditions.elementToBeClickable(btnCarrito));
        btnCarrito.click();
    }
}
