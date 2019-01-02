# Udacity-Project-8-Book-Inventory
An inventory application that stores book information into a database

![](book_inventory_video.gif)

## Requirements

#### Layout
- [x] The app contains activities and/or fragments for the user to:
     - Add inventory
     - See product details
     - Edit product details
     - See a list of all inventory from a Main Activity
- [x] In the Main Activity/Fragment, each list item displays the Product Name, Price, and Quantity.
     - Each list item also contains a Sale Button that reduces the total quantity of that particular product by one (include logic so that no negative quantities are displayed).
- [x] The Product Detail Layout displays the Product Name, Price, Quantity, Supplier Name, and Supplier Phone Number that's stored in the database.
     - The Product Detail Layout also contains buttons that increase and decrease the available quantity displayed.
     - Add a check in the code to ensure that no negative quantities display (zero is the lowest amount).
     - The Product Detail Layout contains a button to delete the product record entirely.
     - The Product Detail Layout contains a button to order from the supplier. In other words, there exists a button to contains a button for the user to contact the supplier via an intent to a phone app using the Supplier Phone Number stored in the database.
     
- [x] When there is no information to display in the database, the layout displays a TextView with instructions on how to populate the database (e.g. what should be entered in the field, which fields are required).
- [x] The code adheres to all of the following best practices:
     - Text sizes are defined in sp
     - Lengths are defined in dp
     - Padding and margin is used appropriately, such that the views are not crammed up against each other.

#### Functionality
- [x] The Main Activity displaying the list of current inventory contains a ListView that populates with the current products stored in the table.
- [x] The Main Activity contains an Add Product Button prompts the user for product information and supplier information which are then properly stored in the table.
     -Before the information is added to the table, it must be validated -
In particular, empty product information is not accepted. If user inputs invalid product information (name, price, quantity, supplier name, supplier phone number), instead of erroring out, the app includes logic to validate that no null values are accepted. If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.
- [x] In the Edit Product Activity, user input is validated. In particular, empty product information is not accepted. If user inputs invalid product information (name, price, quantity, supplier name, supplier phone number), instead of erroring out, the app includes logic to validate that no null values are accepted. If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.
- [x] In the Main Activity that displays a list of all available inventory, each List Item contains a Sale Button which reduces the available quantity for that particular product by one (include logic so that no negative quantities are displayed).
- [x] When a user clicks on a List Item from the Main Activity, it opens up the detail screen for the correct product.
- [x] In the Detail View for each item, there are Buttons that correctly increase or decrease the quantity for the correct product.
     - Add a check in the code to ensure that no negative quantities display (zero is the lowest amount).
     - The student may also add input for how much to increase or decrease the quantity by if not using the default of 1.
- [x] The Detail Layout contains a button for the user to contact the supplier via an intent to a phone app using the Supplier Phone Number stored in the database.
- [x] In the Detail Layout, there is a Delete Button that prompts the user for confirmation and, if confirmed, deletes the product record entirely and sends the user back to the main activity.
- [x] The intent of this project is to give you practice writing raw Java code using the necessary classes provided by the Android framework; therefore, the use of external libraries for core functionality will not be permitted to complete this project.
     - (i.e. Database and content provider libraries are not allowed for this project. Butterknife and similar libraries for ease of coding are allowed)
- [x] The code runs without errors. For example, when user inputs product information (quantity, price, name), instead of erroring out, the app includes logic to validate that no null values are accepted. If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.

 #### Code Readability
- [x] All variables, methods, and resource IDs are descriptively named so that another developer reading the code can easily understand their function.
- [x] The code is properly formatted:
     - No unnecessary blank lines
     - No unused variables or methods
     - No commented out code
- [x] The code also has proper indentation when defining variables and methods.
