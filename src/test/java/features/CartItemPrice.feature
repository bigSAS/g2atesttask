Feature: Shopping cart item pricing

  @Web @G2ACart
  Scenario: Cart item price matches the product page price
    Given User enters G2A.com
    When User searches for game
    And User adds the game to the cart
    Then Cart should contain the game
    And Cart item price should be same as on the product page