Feature: Update product
  Scenario: Update Product Description
    Given User is logged in as admin and is on the products page
    When User selects a product with the name "Combination Pliers"
    And User edits the product name to "updated product name"
    And User edits the product description to "New Product Description"
    And User saves the changes
    Then Product saved message should be displayed
    And The product's name should be "updated product name"