package definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.LoginPage;
import pages.ProductsPage;

public class LoginDefinition {

    LoginPage loginPage;
    ProductsPage productosPage;

    public LoginDefinition() {
        loginPage = new LoginPage();
        productosPage = new ProductsPage();
    }

    @Given("que la web este disponible")
    public void queLaWebEsteDisponible() {
        loginPage.ingresarUrl("https://www.saucedemo.com/v1/index.html");

    }

    @When("ingreso el usuario {string}")
    public void ingresoElUsuario(String user) {
        loginPage.escribirUsuario(user);
    }

    @And("ingreso la clave {string}")
    public void ingresoLaClave(String pass) {
        loginPage.escribirClave(pass);
    }

    @And("click en el boton ingresar")
    public void clickEnElBotonIngresar() {
        loginPage.clickLogin();
    }

    @Then("ingreso a la opción de compras")
    public void ingresoALaOpcionDeCompras() {
        productosPage.validarTitulo();

    }

    @Then("validar mensaje de error {string}")
    public void validarMensajeDeError(String msj) {
        loginPage.validarMensajeError(msj);
    }
}
