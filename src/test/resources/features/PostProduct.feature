Feature: Create new product
  Scenario: Create a new product without name
    Given User is logged as admin and is on add product page
    When User fills in product details without name
    When User submits new product
    Then Error message "Name is required" is displayed
  Scenario: Create a new product with negative price
    Given User is logged as admin and is on add product page
    When User fills in product details with negative price
    When User submits new product
    Then Error message "Price should not be negative" is displayed