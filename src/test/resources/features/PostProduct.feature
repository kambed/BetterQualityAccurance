Feature: Create new product
  Scenario: Create a new product without name
    Given User is logged as admin and is on add product page
    When User fills product description with "Better hammer for better nails"
    And User fills product stock with "1"
    And User fills product price with "12345"
    And User selects product brand "Brand name 1"
    And User selects product category "Hand Tools"
    And User selects product image "Hammer"
    And User submits new product
    Then Error message "Name is required" is displayed
  Scenario: Create a new product with negative price
    Given User is logged as admin and is on add product page
    When User fills product name with "Hammer"
    And User fills product description with "Better hammer for better nails"
    And User fills product stock with "1"
    And User fills product price with "-12345"
    And User selects product brand "Brand name 1"
    And User selects product category "Hand Tools"
    And User selects product image "Hammer"
    And User submits new product
    Then Error message "Price should not be negative" is displayed