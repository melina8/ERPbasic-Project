# ERPbasic
A Desctop Application written in the Java programming language. JavaFX software platform combined with mySQL relational database management system were used for this project.

<kbd><img src="https://user-images.githubusercontent.com/119127267/209887055-2ecb8d94-5deb-4837-90bb-eb4fec5142eb.jpg"
width="600" height="420"></kbd>

## About ##
- What is ERPbasic
- Screenshots from the Desktop Application
             
## What is ERPbasic ##
BasicERP is a desktop application designed for companies that want to manage their business sales processes. It has a dynamic database that stores all the information that the user enters. It basically supports CRUD operations.

Users can create index cards for customers, suppliers, and products by filling out the required fields in the system. They can also create customer orders/supplier orders, add customer receipts, review customer balances, view customer moves, monitor sales and earnings for specified periods of time, track product moves, and manage the storage area based on supplies and purchases. In addition, statistical data can be generated based on user requirements.

All the information can be edited at any time according to the user's needs. The database is dynamically updated and pop-up warning messages appear whenever a change is made to the database.

The application was made in English language.

## Screenshots from the Desctop Application ##

### Main Menu: Supplier ###

<kbd><img src="https://user-images.githubusercontent.com/119127267/209888903-4149ca99-6901-43c0-b1a8-f0fceb7f867e.jpg"
width="600" height="420"></kbd>

We can view, create, edit, or delete a supplier card by selecting 'Create/Edit Supplier' and then using the appropriate buttons on the top, depending on our needs.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210681897-f2382f1e-a732-413a-b22a-ff0cb95a63dd.jpg"
width="400" height="400"></kbd> <kbd><img src="https://user-images.githubusercontent.com/119127267/210681919-517702df-69fd-4481-8100-96a964fb62ba.jpg"
width="400" height="400"></kbd>

<kbd><img src="https://user-images.githubusercontent.com/119127267/210682126-3c3ce315-6200-4216-92a0-9c4f8e4179df.jpg"
width="400" height="400"></kbd> <kbd><img src="https://user-images.githubusercontent.com/119127267/210681936-0cb7bc86-ffe5-4c18-8059-f39a56d2c0b7.jpg"
width="400" height="400"></kbd>

We can add a new supplier order by selecting 'Add Supplier Order'. Numbering is automatically generated. By choosing the Supplier Id from the provided list, the Name is automatically populated. Then, we can select Products, either from the list or by typing their names, one by one. We fill in the Quantities and add them to the table. The Product Description is automatically generated. The program also calculates the Total Items. Finally, we can submit the order to the database or cancel our changes.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210680756-464263c6-be60-438a-bde1-9520e7fe8d78.jpg"
width="1000" height="600"></kbd>

By selecting 'View/Edit Supplier Order', we can select an order from the list of all orders from all suppliers and view, edit or delete the order by using the corresponding buttons. It is also possible to filter the orders by a particular supplier using the drop-down menu in the upper right corner.

<kbd><img src="https://user-images.githubusercontent.com/119127267/209889018-09223b28-8450-4f38-8d52-0da24769a9b9.jpg"
width="400" height="600"></kbd>   <kbd><img src="https://user-images.githubusercontent.com/119127267/210017524-3eab37e4-5e72-4799-bbcf-5e9780fd0176.jpg"
width="400" height="600"></kbd>

This is the appearance when we select to edit a specific order from the list.

<kbd><img src="https://user-images.githubusercontent.com/119127267/209889059-b7ef4173-ed86-4ce2-b4f9-d19ec9f9d02d.jpg"
width="1000" height="600"></kbd>


### Main Menu: Products ###

<kbd><img src="https://user-images.githubusercontent.com/119127267/210022072-2f24d852-ab05-41de-bead-c14a0e61141a.jpg"
width="600" height="420"></kbd>

Similarly, we can create a new product by linking it with an existing supplier and filling out all the required fields, including the tax percentage. The final tax is automatically calculated. We can also edit, delete, or view an existing product.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210022253-a8d6c4e9-f2db-461d-8bc3-862be68235a6.jpg"
width="400" height="400"></kbd> <kbd><img src="https://user-images.githubusercontent.com/119127267/210022260-4c450e27-dda4-4557-a330-af73446ef34d.jpg"
width="400" height="400"></kbd>

<kbd><img src="https://user-images.githubusercontent.com/119127267/210022268-647ce2c9-34b3-483d-ade0-866ba36e7d2a.jpg"
width="400" height="400"></kbd> <kbd><img src="https://user-images.githubusercontent.com/119127267/210022270-08170f26-aa26-4be9-87cd-87e271ebed7e.jpg"
width="400" height="400"></kbd>

By using 'Stock', we can check all the products and their current quantities. The stock is dynamic and updates according to supplies and purchases.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210022816-eecd29a0-5a2b-4d6b-956d-8429bf7a8571.jpg"
width="1000" height="600"></kbd>

Finally, 'Product Moves' allows us to view the movements of a particular product, which we select from a list. It displays all supplies and purchases of the specific product, in chronological order. We also have the ability to open an order from the list in an editing format, either by double-clicking it or by selecting it and pressing the 'Open Order' button. We can use the 'Refresh' button to update the list if we have made any changes.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210023459-3358a313-1ff6-42c7-9825-c4e2410392f5.jpg"
width="1000" height="600"></kbd>


### Main Menu: Customer ###

<kbd><img src="https://user-images.githubusercontent.com/119127267/210288418-fe48f35d-a955-465c-a7ca-ca4ae88ff48c.jpg"
width="600" height="420"></kbd>

Similarly to the supplier, we can view, create, edit, or delete a customer card by selecting 'Create/Edit Customer' and then using the appropriate buttons on the top, depending on our needs. When adding a new customer, we have to follow specific rules. In case of a problem, a pop-up menu will inform us of how to fix it.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210288760-a97be048-ea9a-4bb0-bab0-9c7aa47df684.jpg"
width="400" height="400"></kbd> <kbd><img src="https://user-images.githubusercontent.com/119127267/210288766-06490b60-0dd8-4bb8-bce5-7671469fd5c0.jpg"
width="400" height="400"></kbd>

<kbd><img src="https://user-images.githubusercontent.com/119127267/210288770-95db55ae-c8a9-4f28-b150-37942b604486.jpg"
width="400" height="400"></kbd> <kbd><img src="https://user-images.githubusercontent.com/119127267/210288778-6104cb11-3611-41d3-92e1-4b161fefa836.jpg"
width="400" height="400"></kbd>

We can add a new customer order by selecting 'Add Shipping Order' from the Customer Menu. Numbering is automatically generated. By choosing the Customer Id from the provided list, the Name is automatically populated. Then, we can select products, either from the list or by typing their names, one by one. We fill in the Quantities and the Discount (this is optional), and add them to the table. The Product Description, Unit Cost, Tax, and Final Cost are automatically generated. The program also calculates the Total Items, Total Unit Cost, Total Tax, and the Final Cost. Finally, we can submit the order to the database or cancel any changes.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210288819-a8ad2e5c-2cff-48c2-9f40-92f70c6dced4.jpg"
width="1000" height="600"></kbd>

In case we miss something while creating or editing an order, warning messages will prompt us to fill out any missing parts. Additionally, if we exceed the request for an item, warning messages will pop up and ask us to adjust the quantity based on the number of items remaining in stock.

<kbd><img src="https://user-images.githubusercontent.com/119127267/214696767-ae57ca13-b90c-4765-94d5-eebdf3036feb.jpg"
width="1000" height="600"></kbd>

By selecting the 'View/Edit Shipping Order' feature, we can select an order from the list of all orders from all customers and view, edit, or delete the order by using the corresponding buttons. It is also possible to filter the orders by a particular customer using the drop-down menu in the upper right corner.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210288827-9c1ccbdb-d79f-4cb2-a7ea-6f2b4af6bc16.jpg"
width="400" height="600"></kbd>   <kbd><img src="https://user-images.githubusercontent.com/119127267/210288831-a2ac9d4a-f9e7-4b15-aca6-3ce5530ff758.jpg"
width="400" height="600"></kbd>

This is the appearance when we select to edit a specific order from the list:

<kbd><img src="https://user-images.githubusercontent.com/119127267/210288838-a3bdb550-24b0-47b8-af43-59c1dc753975.jpg"
width="1000" height="600"></kbd>

By selecting the 'Sales' feature, a list of all sales within the selected duration of time appears. The total sales for the chosen time period is also calculated and displayed in the bottom-right field. We can open any order in an editing format, either by double-clicking it or by selecting it and pressing the 'Open Order' button.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210674560-e1f6ee32-4409-4baf-abf3-031dd9e65b1f.jpg"
width="1000" height="600"></kbd>

We can also add receipts from our customers by using the 'Add Customer Receipts' feature. It allows us to enter multiple receipts for a particular date. We choose the Date, select the Customer Id, enter the Amount, choose the Type of Payment, and add Notes (optional) for each customer. Numbering and the Customer Name are automatically generated. The total amount of receipts is calculated and displayed in the bottom field.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210675136-075d6b3f-9b8f-4578-98ea-c05398d99d05.jpg"
width="1000" height="500"></kbd>

With the 'Customer Receipt List,' we can view a list of registered receipts for a specific duration of time that we choose. We can also edit or delete a receipt by selecting the receipt and pressing the corresponding buttons (EDIT RECEIPT/DELETE RECEIPT). 

<kbd><img src="https://user-images.githubusercontent.com/119127267/210675148-981a2326-ea22-45fd-8701-d5ea6d330043.jpg"
width="500" height="600"></kbd>

This is the appearance when we select to edit a specific receipt from the list:

<kbd><img src="https://user-images.githubusercontent.com/119127267/210675171-ad02f1c1-07c6-49d1-9d8e-b91ec2b97b5c.jpg"
width="1000" height="200"></kbd>

By selecting 'Earnings,' a list of all receipts appears, concerning the duration of time that we choose. The total amount for the selected time is also calculated and presented at the bottom-right field. We are capable of opening any receipt in an editing format either by double-clicking it or by selecting it and pressing the 'Open Receipt' button.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210677141-f912472c-5283-47c2-8b50-ff7e33dd32bf.jpg"
width="1000" height="600"></kbd>

The 'Balances' feature allows us to review Total Costs, Total Receipts, and resulting Balances for all of our customers. We can order the customers by any column by simply clicking on the title of the column. The image below illustrates an order based on Balances (highest to lowest). By re-clicking, the order is reversed.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210677895-e68a5652-464c-469e-9578-4668298bc59b.jpg"
width="1000" height="600"></kbd>

Additionally, the 'Customer Moves' feature shows us the moves of a particular Customer, that we choose from a list. It displays all Orders and Receipts from the specific customer in chronological order. We also have the ability to open an order/receipt  from the list in an editing format, either by double-clicking it or by selecting it and pressing the "Open Order/Receipt" button. We can use the 'Refresh' button to refresh the list in case we made any changes. The customer's balance also appears in the bottom-right field.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210677913-5ea3e6b9-a24c-429f-a793-0c4c9fdc5fbd.jpg"
width="1000" height="600"></kbd>


### Main Menu: Statistics ###

<kbd><img src="https://user-images.githubusercontent.com/119127267/210158212-bc8852e2-6e51-417e-baa1-32fd29e788e1.jpg"
width="600" height="420"></kbd>

A number of Statistics can be calculated and presented in bar charts and pie charts, as follows: <br />
Number of products per supplier.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210158214-82633f6a-f4fa-4ad7-9838-05f5e6424cfc.jpg"
width="500" height="420"></kbd>

Sales per month, depicting all months from all the years so that we can compare sales for the same month for different years as well as sales for different months for the same year.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210158216-5b8aa021-409d-401b-830a-99969a15a066.jpg"
width="1000" height="600"></kbd>

Best 5 selling products, for the duration of time that we choose according to our needs.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210158254-d6778041-aa62-4cae-88ed-89aab7de5391.jpg"
width="500" height="600"></kbd>

Best 5 customers, for the duration of time that we choose according to our needs.

<kbd><img src="https://user-images.githubusercontent.com/119127267/210158219-b8558f5b-d65e-475e-bd3c-7a2539568cf4.jpg"
width="600" height="600"></kbd>
