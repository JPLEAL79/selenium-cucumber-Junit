package definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.CartPage;
import pages.CheckoutPage;
import pages.ProductsPage;

public class ProductPurchasesDefinition {

    ProductsPage productosPage;
    CartPage carritoPage;
    CheckoutPage checkoutPage;

    public ProductPurchasesDefinition() {
        productosPage = new ProductsPage();
        carritoPage = new CartPage();
        checkoutPage = new CheckoutPage();
    }

    @Given("agregar producto {string} al carrito")
    public void agregarProductoAlCarrito(String producto) {
        productosPage.clickCarrito();

    }

    @And("ingresamos al carrito de compras")
    public void ingresamosAlCarritoDeCompras() {
        productosPage.clickCarrito();
    }

    @And("realizamos en checkout")
    public void realizamosEnCheckout() {
        carritoPage.clickCheckout();
    }

    @When("ingresamos el nombre del cliente {string}")
    public void ingresamosElNombreDelCliente(String nombre) {
        checkoutPage.escribirNombre(nombre);
    }

    @And("ingresamos el apellido del cliente {string}")
    public void ingresamosElApellidoDelCliente(String apellido) {
        checkoutPage.escribirApellido(apellido);
    }

    @And("ingresamos el código postal {string}")
    public void ingresamosElCodigoPostal(String codigo) {
        checkoutPage.escribirCodigoPostal(codigo);
    }

    @And("confirmamos los datos ingresados")
    public void confirmamosLosDatosIngresados() {
        checkoutPage.clickContinuar();

    }

    @And("confirmamos el pago del producto")
    public void confirmamosElPagoDelProducto() {
    }

    @Then("validamos el mensaje de confirmación")
    public void validamosElMensajeDeConfirmacion() {
    }
}
