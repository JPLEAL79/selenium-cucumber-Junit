Feature: Funcionalidad comprar

  Background: Login
    Given que la web este disponible
    When ingreso el usuario "standard_user"
    And ingreso la clave "secret_sauce"
    And click en el boton ingresar
    Then ingreso a la opción de compras

    @ComprarProducto
  Scenario: Comprar un producto
    Given agregar producto "Sauce Labs Fleece Jacket" al carrito
    And ingresamos al carrito de compras
    And realizamos en checkout
    When ingresamos el nombre del cliente "Juan Pablo"
    And ingresamos el apellido del cliente "Leal"
    And ingresamos el código postal "1234"
    And confirmamos los datos ingresados
    And confirmamos el pago del producto
    Then validamos el mensaje de confirmación