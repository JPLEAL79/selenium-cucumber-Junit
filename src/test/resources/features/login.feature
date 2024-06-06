Feature: Funcionalidad web login

  @Login
  Scenario: Login Exitoso
    Given que la web este disponible
    When ingreso el usuario "standard_user"
    And ingreso la clave "secret_sauce"
    And click en el boton ingresar
    Then ingreso a la opción de compras

  @Login
  Scenario Outline: Login fallido
    Given que la web este disponible
    When ingreso el usuario "<usuario>"
    And ingreso la clave "<clave>"
    And click en el boton ingresar
    Then validar mensaje de error "<mensaje>"

    Examples:
      | usuario         | clave        | mensaje                                                                   |
      | locked_out_user | secret_sauce | Epic sadface: Sorry, this user has been locked out.                       |
      | aavilav         | 1234567890   | Epic sadface: Username and password do not match any user in this service |