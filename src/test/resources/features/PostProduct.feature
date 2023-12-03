Feature: Create new product
  Scenario: Create a new product without name
    Given User is logged as admin and is on add product page
    When User fills in product details without name
    Then Error message is displayed