import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.sql.*;
import java.text.DecimalFormat;


public class AddCusOrderForm {
    Stage stage;
    Label lblShip_no, lblDate, lblCust_id, lblCustName, lblq, lblSubTotal, lblTotalTax, lblTotal;
    TextField txtShip_no, txtCustName, txtDescription, txtQuantity, txtUnitCost, txtTax, txtDiscount, txtq, txtSubTotal, txtTotalTax,txtTotal;
    DatePicker datepicker;
    ComboBox<String> comboCustId;
    ComboBox<String> txtProductName;
    Button btnAddProduct, btnDeleteLine, btnSubmit, btnCancel;
    HBox hboxInfo, hboxProduct, hboxItems, hboxSubTotal, hboxTax, hboxTotal, hBoxSelect;
    VBox vBoxTotals;

    TableView<OrdItems> tableView;
    TableColumn<OrdItems, String> productNameColumn;
    TableColumn<OrdItems, String> descriptionColumn;
    TableColumn<OrdItems, Double> costColumn;
    TableColumn<OrdItems, Double> taxColumn;
    TableColumn<OrdItems, Integer> discountColumn;
    TableColumn<OrdItems, Integer> itemsColumn;
    TableColumn<OrdItems, Double> totalCostColumn;

    int total = 0;
    double subtotal = 0;
    double totalTax = 0;
    double totalAmount = 0;

    double valueWithTax;
    String resultCustId, resultProductName;

    //ArrayList<Integer> productStorageList;
    ObservableList<Integer>  productStorageList = FXCollections.observableArrayList();
    //ArrayList<Integer> productTableViewList;
    ObservableList<Integer>  productTableViewList = FXCollections.observableArrayList();
    //ArrayList<String> productList;
    ObservableList<String>  productList = FXCollections.observableArrayList();
    //ArrayList<String> productList2;
    ObservableList<String>  productList2 = FXCollections.observableArrayList();

    Connection connection;


    public AddCusOrderForm(Connection conn) {
        connection = conn;


        //grid
        GridPane grid = new GridPane();
        grid.setHgap(7);
        grid.setVgap(7);


        //Ship_no
        lblShip_no = new Label("No:");
        txtShip_no = new TextField();
        txtShip_no.setPrefWidth(70);
        txtShip_no.setEditable(false);


        lblDate = new Label("Date:");
        datepicker = new DatePicker();


        lblCust_id = new Label("Customer Id:");
        comboCustId = new ComboBox<String>();
        comboCustId.setEditable(true);
        new AutoCompleteComboBoxListener<>(comboCustId);
        comboCustId.setPrefWidth(100);


        lblCustName = new Label("Customer Name:");
        txtCustName = new TextField();
        txtCustName.setPrefWidth(200);


        hboxInfo = new HBox();
        hboxInfo.getChildren().addAll(lblShip_no, txtShip_no, lblDate, datepicker, lblCust_id, comboCustId, lblCustName, txtCustName);
        hboxInfo.setSpacing(30);
        hboxInfo.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(hboxInfo, 7);
        grid.add(hboxInfo, 0, 0);


        txtProductName = new ComboBox<String>();
        txtProductName.setEditable(true);
        new AutoCompleteComboBoxListener<>(txtProductName);
        txtProductName.setPromptText("enter product name");

        txtDescription = new TextField();
        txtDescription.setPromptText("product description");
        txtDescription.setPrefWidth(250);

        txtUnitCost = new TextField();
        txtUnitCost.setPromptText("unit cost");

        txtTax = new TextField();
        txtTax.setPromptText("tax");

        txtDiscount = new TextField();
        txtDiscount.setPromptText("enter discount(%)");

        txtQuantity = new TextField();
        txtQuantity.setPromptText("enter quantity");


        btnAddProduct = new Button("Add Product");
        btnDeleteLine = new Button("Delete Selected Line(s)");


        hboxProduct = new HBox();
        hboxProduct.getChildren().addAll(txtProductName, txtDescription, txtUnitCost, txtTax, txtDiscount, txtQuantity, btnAddProduct, btnDeleteLine);
        hboxProduct.setSpacing(10);
        hboxProduct.setMinHeight(100);
        hboxProduct.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(hboxProduct, 7);
        grid.add(hboxProduct, 0, 1);

        //grid constraints
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPrefWidth(200);
        col0.setHalignment(HPos.LEFT);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.SOMETIMES);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.SOMETIMES);

        Region rEmpty = new Region();
        grid.add(rEmpty, 3, 0);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.SOMETIMES);


        grid.getColumnConstraints().addAll(col0, col1, col2, col3);


        //query for ship_no:
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM shipping_orders ORDER BY ship_no DESC LIMIT 1");

            //check if table is empty
            if (!resultSet.next()) {
                System.out.println("Table is empty");
                txtShip_no.setText("1");
            } else {

                do {
                    CustOrder sho = new CustOrder(
                            resultSet.getInt("ship_no"),
                            resultSet.getString("cus_id"),
                            resultSet.getString("customer_name"),
                            resultSet.getDate("ship_date").toLocalDate()
                    );
                    String recno = sho.getNumber();
                    int no = Integer.parseInt(recno);

                    int new_no = no += 1;
                    String new_rec = String.valueOf((Integer) new_no);
                    txtShip_no.setText(new_rec);
                    System.out.println(new_rec);


                } while (resultSet.next());
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        //fill the combobox - customer id and fill the customer name automatically
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM customer ORDER BY cus_id");

            //add an empty item just for the "no-choice"
            comboCustId.getItems().add("");
            while (resultSet.next()) {


                Customer c = new Customer(
                        resultSet.getString("cus_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email")
                );

                comboCustId.getItems().add(c.getId());


                comboCustId.setOnAction(e -> {
                    resultCustId = comboCustId.getValue();
                    System.out.println(resultCustId);
                    try {
                        CallableStatement callstmt = conn.prepareCall("call input2(?)");
                        callstmt.setString(1, resultCustId);

                        ResultSet newresult = callstmt.executeQuery();

                        while(newresult.next()){
                            System.out.println(newresult.getString("customer_name"));
                            txtCustName.setText(newresult.getString("customer_name"));
                        }
                        newresult.close();
                        callstmt.close();
                    } catch (SQLException d) {
                        d.printStackTrace();
                    }
                });


            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        //tableView
        productNameColumn = new TableColumn<>("Product Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.setMinWidth(200);

        descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setMinWidth(500);

        costColumn = new TableColumn<>("Unit Cost");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        costColumn.setMinWidth(150);

        taxColumn = new TableColumn<>("Tax");
        taxColumn.setCellValueFactory(new PropertyValueFactory<>("productTax"));
        taxColumn.setMinWidth(150);

        discountColumn = new TableColumn<>("Discount(%)");
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        discountColumn.setMinWidth(150);

        itemsColumn = new TableColumn<>("Quantity");
        itemsColumn.setCellValueFactory(new PropertyValueFactory<>("items"));
        itemsColumn.setMinWidth(150);

        totalCostColumn = new TableColumn<>("Final Cost");
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("finalCost"));
        totalCostColumn.setMinWidth(210);


        tableView = new TableView<>();
        tableView.getColumns().add(productNameColumn);
        tableView.getColumns().add(descriptionColumn);
        tableView.getColumns().add(costColumn);
        tableView.getColumns().add(taxColumn);
        tableView.getColumns().add(discountColumn);
        tableView.getColumns().add(itemsColumn);
        tableView.getColumns().add(totalCostColumn);

        tableView.setEditable(true);

        productNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        costColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        taxColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        discountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        itemsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        totalCostColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));


        productNameColumn.setEditable(false);
        descriptionColumn.setEditable(false);


        itemsColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<OrdItems, Integer>>() {

            @Override
            public void handle(TableColumn.CellEditEvent<OrdItems, Integer> event) {
                OrdItems orditem = event.getRowValue();
                int newItems = event.getNewValue();
                orditem.setItems(event.getNewValue());
                Integer pDiscount = orditem.getDiscount();
                Double uCost = orditem.getUnitCost();
                Double pTax = orditem.getProductTax();
                Double newFinalCost2 = ((uCost+pTax)-((uCost)*pDiscount)/100)*newItems;
                DecimalFormat df = new DecimalFormat("#.00");
                Double newFinalCost = Double.parseDouble(df.format(newFinalCost2));

                orditem.setFinalCost(newFinalCost);
                System.out.println("finalcost: " + newFinalCost);
                totalCostColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

                calculateTotals();

            }
        });

        discountColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<OrdItems, Integer>>() {

            @Override
            public void handle(TableColumn.CellEditEvent<OrdItems, Integer> event){

                OrdItems orditem = event.getRowValue();
                Integer newDiscount = event.getNewValue();
                orditem.setDiscount(event.getNewValue());
                System.out.println("oldfinalcost: " + orditem.getFinalCost());
                int pItems = orditem.getItems();
                Double uCost = orditem.getUnitCost();
                Double pTax = orditem.getProductTax();
                Double newFinalCost2 = ((uCost+pTax)-((uCost)*newDiscount)/100)*pItems;
                DecimalFormat df = new DecimalFormat("#.00");
                Double newFinalCost = Double.parseDouble(df.format(newFinalCost2));

                orditem.setFinalCost(newFinalCost);
                System.out.println("finalcost: " + newFinalCost);
                totalCostColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

                calculateTotals();
            }
        });

        tableView.setPrefWidth(700);
        tableView.setPrefHeight(900);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10,10,10,10));
        vbox.getChildren().addAll(grid, tableView);


        //Total Items
        lblq =  new Label("Total Items:");
        txtq = new TextField();
        txtq.setPrefWidth(145);

        hboxItems = new HBox();
        hboxItems.getChildren().addAll( lblq, txtq);
        hboxItems.setSpacing(10);
        hboxItems.setAlignment(Pos.CENTER_RIGHT);

        //SubTotal
        lblSubTotal = new Label("Subtotal:");
        txtSubTotal = new TextField();
        txtSubTotal.setPrefWidth(145);

        hboxSubTotal = new HBox();
        hboxSubTotal.getChildren().addAll( lblSubTotal, txtSubTotal);
        hboxSubTotal.setSpacing(10);
        hboxSubTotal.setAlignment(Pos.CENTER_RIGHT);

        //TotalTax
        lblTotalTax = new Label("Total Tax:");
        txtTotalTax = new TextField();
        txtTotalTax.setPrefWidth(145);

        hboxTax = new HBox();
        hboxTax.getChildren().addAll( lblTotalTax, txtTotalTax);
        hboxTax.setSpacing(10);
        hboxTax.setAlignment(Pos.CENTER_RIGHT);

        //Total amount
        lblTotal = new Label("TOTAL:");
        txtTotal = new TextField();
        txtTotal.setPrefWidth(145);

        hboxTotal = new HBox();
        hboxTotal.getChildren().addAll( lblTotal, txtTotal);
        hboxTotal.setSpacing(10);
        hboxTotal.setAlignment(Pos.CENTER_RIGHT);

        vBoxTotals = new VBox();
        vBoxTotals.getChildren().addAll( hboxItems, hboxSubTotal, hboxTax, hboxTotal);
        vBoxTotals.setPadding(new Insets(0,10,0,0));


        //cancel button
        btnCancel = new Button();
        btnCancel.setText("Cancel");
        btnCancel.setOnAction(e->{
            datepicker.getEditor().clear();
            comboCustId.setValue(null);
            txtCustName.setText("");
            txtProductName.setValue(null);
            txtDescription.clear();
            txtUnitCost.clear();
            txtTax.clear();
            txtDiscount.clear();
            txtQuantity.clear();
            tableView.getItems().clear();
            txtq.clear();
            txtSubTotal.clear();
            txtTotalTax.clear();
            txtTotal.clear();
            total = 0;
            subtotal = 0;
            totalTax = 0;
            totalAmount = 0;

        });

        //submit button
        btnSubmit = new Button("Submit");
        btnSubmit.setOnAction(e-> {

            //check if all fields are filled in
            if (datepicker.getValue() == null){
                MessageBoxOK mb = new MessageBoxOK("Please pick a Date.", "WARNING!");
                System.out.println("Please pick a Date.");
            }
            else if (comboCustId.getValue() == null){
                MessageBoxOK mb = new MessageBoxOK("Please choose Customer Id.", "WARNING!");
                System.out.println("Please choose Customer Id.");
            }
            else if(tableView.getItems().size() == 0){
                MessageBoxOK mb = new MessageBoxOK("Table is empty! Please add products.", "WARNING!");
                System.out.println("Table is empty! Please add products.");
            }else if (!checkAvailability()) {
                //MessageBoxOK mb = new MessageBoxOK("Products out of stock", "WARNING!");
                System.out.println("Products out of stock.");
            }
            //everything is ok! add data to DB
            else {

                //update shipping_orders table on the DB
                CustOrder sho = new CustOrder();

                sho.setNumber(txtShip_no.getText());
                sho.setCustomerId(comboCustId.getValue());
                sho.setCustomerName(txtCustName.getText());
                sho.setDate(datepicker.getValue());

                try {
                    String insQuery = "INSERT INTO shipping_orders" +
                            " (ship_no, cus_id, customer_name, ship_date)" +
                            " VALUES(?,?,?,?)";

                    PreparedStatement insStmt = conn.prepareStatement(insQuery);
                    insStmt.setInt(1, Integer.parseInt(sho.getNumber()));
                    insStmt.setString(2, sho.getCustomerId());
                    insStmt.setString(3, sho.getCustomerName());
                    insStmt.setObject(4, sho.getDate());
                    insStmt.executeUpdate();

                    insStmt.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


                //update ordered_items on DB (one record for each row of the table)
                for (int i = 0; i < tableView.getItems().size(); i++) {
                    OrdItems ori = new OrdItems();
                    ori.setShipOrder_no(Integer.parseInt(txtShip_no.getText()));
                    System.out.println(Integer.parseInt(txtShip_no.getText()));

                    ori.setProductName(productNameColumn.getCellData(i));
                    System.out.println(productNameColumn.getCellData(i));

                    ori.setDescription(descriptionColumn.getCellData(i));
                    System.out.println(descriptionColumn.getCellData(i));

                    ori.setItems(itemsColumn.getCellData(i));
                    System.out.println(itemsColumn.getCellData(i));

                    ori.setUnitCost(costColumn.getCellData(i));
                    System.out.println(costColumn.getCellData(i));

                    ori.setProductTax(taxColumn.getCellData(i));
                    System.out.println(taxColumn.getCellData(i));

                    ori.setDiscount(discountColumn.getCellData(i));
                    System.out.println((discountColumn.getCellData(i)));

                    ori.setFinalCost(totalCostColumn.getCellData(i));
                    System.out.println((totalCostColumn.getCellData(i)));

                    try {
                        String insQuery = "INSERT INTO ordered_items" +
                                " (ship_no, productname, pieces, unitCost, tax, discount, finalCost)" +
                                " VALUES(?,?,?,?,?,?,?)";

                        PreparedStatement insStmt = conn.prepareStatement(insQuery);
                        insStmt.setInt(1, Integer.parseInt(sho.getNumber()));
                        insStmt.setString(2, ori.getProductName());
                        insStmt.setInt(3, ori.getItems());
                        insStmt.setDouble(4, ori.getUnitCost());
                        insStmt.setDouble(5, ori.getProductTax());
                        insStmt.setInt(6, ori.getDiscount());
                        insStmt.setDouble(7, ori.getFinalCost());
                        System.out.println("Values are: " + sho.getNumber() + " " +  ori.getProductName() + " " + ori.getItems() + " " +ori.getUnitCost() + " " +ori.getProductTax() + " " +ori.getDiscount() + "  " +ori.getFinalCost());
                        insStmt.executeUpdate();

                        insStmt.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }

                updateStorage();


                MessageBoxOK mb = new MessageBoxOK("Order entered successfully.", "INFORMATION!");
                stage.close();

            } //else
        });

        hBoxSelect = new HBox();
        hBoxSelect.getChildren().addAll(btnCancel, btnSubmit);
        hBoxSelect.setAlignment(Pos.CENTER);
        hBoxSelect.setSpacing(50);
        hBoxSelect.setMinHeight(50);

        VBox pane = new VBox();
        pane.getChildren().addAll(vbox, vBoxTotals, hBoxSelect);

        //add products on the tableView...
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultProduct = stmt.executeQuery("SELECT p.* FROM products p INNER JOIN (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime");

            //add an empty item just for the "no-choice"
            txtProductName.getItems().add("");
            while (resultProduct.next()) {


                Product p = new Product(
                        resultProduct.getString("sup_id"),
                        resultProduct.getString("productname"),
                        resultProduct.getString("productdescription"),
                        resultProduct.getDouble("unitCost"),
                        resultProduct.getString("taxRate"),
                        resultProduct.getDouble("tax"),
                        resultProduct.getTimestamp("time_changed")
                );

                txtProductName.getItems().add(p.getProductName());

                txtProductName.setOnAction(e -> {

                    resultProductName = txtProductName.getValue();

                    System.out.println(resultProductName);
                    try {
                        CallableStatement callstmt = conn.prepareCall("call fillDescription2(?)");
                        callstmt.setString(1, resultProductName);

                        ResultSet newresult = callstmt.executeQuery();

                        while(newresult.next()){
                            System.out.println(newresult.getString("productdescription"));

                            txtDescription.setText(newresult.getString("productdescription"));
                            txtUnitCost.setText(String.valueOf(newresult.getDouble("unitCost")));
                            txtTax.setText(String.valueOf(newresult.getDouble("tax")));
                            valueWithTax = Double.parseDouble(txtUnitCost.getText()) + Double.parseDouble(txtTax.getText());
                            System.out.println("valueWithTax: " + valueWithTax);
                        }
                        newresult.close();
                        callstmt.close();
                    } catch (SQLException d) {
                        d.printStackTrace();
                    }
                });

            }
            resultProduct.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //...by filling tableView lines
        btnAddProduct.setOnAction(e -> {

            //check if all fields are filled in
            if (txtProductName.getValue() == null){
                MessageBoxOK mb = new MessageBoxOK("Please pick a Product.", "WARNING!");
                System.out.println("Please pick a Product.");
            }
            else if (txtQuantity.getText().isEmpty()) {
                MessageBoxOK mb = new MessageBoxOK("Please enter number of items.", "WARNING!");
                System.out.println("Please enter number of items.");
            }

            //everything is ok! add productline to the table
            else {

                if (txtDiscount.getText().isEmpty()) {
                    System.out.println("No Discount");
                    txtDiscount.setText("0");
                }

                System.out.println("Values!!");
                String firstParam = txtProductName.getValue();
                System.out.println(firstParam);
                String secondParam = txtDescription.getText();
                System.out.println(secondParam);
                Double thirdParam = Double.parseDouble(txtUnitCost.getText());
                System.out.println(thirdParam);
                Double forthParam = Double.parseDouble(txtTax.getText());
                System.out.println(forthParam);
                Integer fifthParam = Integer.parseInt(txtDiscount.getText());
                System.out.println(fifthParam);
                int sixParam = Integer.parseInt(txtQuantity.getText());
                System.out.println(sixParam);
                Double sevenParam2 = (valueWithTax - (valueWithTax * fifthParam) / 100) * sixParam;
                DecimalFormat df = new DecimalFormat("#.00");
                Double sevenParam = Double.parseDouble(df.format(sevenParam2));
                System.out.println(sevenParam);

                OrdItems productLine = new OrdItems(firstParam, secondParam, thirdParam, forthParam, fifthParam, sixParam, sevenParam);
                tableView.getItems().add(productLine);
                txtProductName.setValue("");
                txtQuantity.clear();
                txtDescription.clear();
                txtUnitCost.clear();
                txtTax.clear();
                txtDiscount.clear();

                calculateTotals();
            }
        });


        btnDeleteLine.setOnAction(e -> {
            if  (tableView.getSelectionModel().getSelectedCells().size() > 1) {
                System.out.println("multiple");
                tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItems());
            }
            else {
                System.out.println("single");
                tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItem());
            }
            calculateTotals();
        });


        //move cursor with enter key
        txtShip_no.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                datepicker.requestFocus();
            }
        });
        datepicker.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                comboCustId.requestFocus();
            }
        });
        comboCustId.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtCustName.requestFocus();
            }
        });
        txtCustName.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtProductName.requestFocus();
            }
        });
        txtProductName.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtDescription.requestFocus();
            }
        });
        txtDescription.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtUnitCost.requestFocus();
            }
        });
        txtUnitCost.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtTax.requestFocus();
            }
        });
        txtTax.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtDiscount.requestFocus();
            }
        });
        txtDiscount.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtQuantity.requestFocus();
            }
        });
        txtQuantity.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                btnAddProduct.requestFocus();
            }
        });


        // the scene
        Scene scene = new Scene(pane);
        scene.heightProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pane.setPrefHeight(scene.getHeight());

                });
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pane.setPrefWidth(scene.getWidth());
                });

        //the stage
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add Customer Order");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 700) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 600) / 2);
        stage.setWidth(700);
        stage.setHeight(600);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);
        stage.show();

    }

    public void calculateTotals(){

        //calculate total items
        for (int i=0; i<tableView.getItems().size(); i++){
            total   += itemsColumn.getCellData(i);
            System.out.println("items " + total);
        }
        System.out.println("total items: " + total);
        txtq.setText(String.valueOf(total));
        total = 0;

        //calculate subtotal amount
        for (int i=0; i<tableView.getItems().size(); i++){
            subtotal   += (costColumn.getCellData(i) - costColumn.getCellData(i)*(discountColumn.getCellData(i))/100)*itemsColumn.getCellData(i);
        }
        System.out.println("subtotal: " + subtotal);

        DecimalFormat dfSubTotal = new DecimalFormat("#.00");
        Double subtotalTrim = Double.parseDouble(dfSubTotal.format(subtotal));
        System.out.println("subtotal " + subtotalTrim);
        txtSubTotal.setText(String.valueOf(subtotalTrim));
        subtotal = 0;

        //calculate totaltax amount
        for (int i=0; i<tableView.getItems().size(); i++){
            totalTax  += taxColumn.getCellData(i)*itemsColumn.getCellData(i);

        }
        System.out.println("totalTax: " + totalTax);

        DecimalFormat dfSubTotalTax = new DecimalFormat("#.00");
        Double totalTaxTrim = Double.parseDouble(dfSubTotalTax.format(totalTax));
        System.out.println("totalTax " + totalTaxTrim);
        txtTotalTax.setText(String.valueOf(totalTaxTrim));
        totalTax = 0;

        //calculate total amount
        for (int i=0; i<tableView.getItems().size(); i++){
            totalAmount  += totalCostColumn.getCellData(i);

        }
        System.out.println("totalAmount: " + totalAmount);

        DecimalFormat dfTotalAmount = new DecimalFormat("#.00");
        Double totalAmountTrim = Double.parseDouble(dfTotalAmount.format(totalAmount));
        System.out.println("totalAmount " + totalAmountTrim);
        txtTotal.setText(String.valueOf(totalAmountTrim));
        totalAmount = 0;
    }


    public void updateStorage(){

        productStorageList.clear();
        productTableViewList.clear();
        productList.clear();
        productList2.clear();

        //update storage area on DB (one record for each row of the table)

        //search in the storage area and find the values of the items listing in the tableview (previous values)
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT r.productname, p.productdescription, s.pieces, s.sup_id \n" +
                    "         FROM storage_area s JOIN products p JOIN ordered_items r  INNER JOIN \n" +
                    "                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime" +
                    "        WHERE  r.productname = p.productname AND s.productname = r.productname AND r.ship_no = '" + txtShip_no.getText() + "'" +
                    "        ORDER BY productName");

            while (resultSet.next()) {

                Stock st = new Stock(
                        resultSet.getString("productName"),
                        resultSet.getString("productdescription"),
                        resultSet.getInt("pieces"),
                        resultSet.getString("sup_id")
                );

                productStorageList.add(st.getItems());
                productList.add(st.getProductName());
                System.out.println("=========================");


            }
            for(int i=0; i<productStorageList.size(); i++){
                System.out.println("previous item" + i + ": " + productStorageList.get(i));
                System.out.println("previous product" + i + ": " + productList.get(i));
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //search into the ordered_items and find the values of the items of the current tableview (current values)
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT r.productname, p.productdescription, r.pieces, p.sup_id \n" +
                    "         FROM ordered_items r JOIN products p  INNER JOIN \n" +
                    "                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime" +
                    "         WHERE  r.productname = p.productname AND r.ship_no = '" + txtShip_no.getText() + "'" +
                    "         ORDER BY r.productName");

            while (resultSet.next()) {

                Stock st = new Stock(
                        resultSet.getString("productName"),
                        resultSet.getString("productdescription"),
                        resultSet.getInt("pieces"),
                        resultSet.getString("sup_id")
                );

                productTableViewList.add(st.getItems());
                productList2.add(st.getProductName());
                System.out.println("=========================");


            }
            for(int i=0; i<productTableViewList.size(); i++){
                System.out.println("new item" + i + ": " + productTableViewList.get(i));
                System.out.println("new product" + i + ": " + productList2.get(i));
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        //for each row of the tableView subtract number of products from the storage_area table respectively
        for (int i = 0; i < tableView.getItems().size(); i++) {
            try {
                String updQuery = "UPDATE storage_area" +
                        " SET pieces = pieces - ? " +
                        " WHERE productName = ? ";

                PreparedStatement updStmt = connection.prepareStatement(updQuery);
                updStmt.setInt(1, (productTableViewList.get(i)));
                updStmt.setString(2, productList.get(i));
                updStmt.executeUpdate();
                System.out.println("---------------------");

                updStmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        } //end of for
    } //end of checkAvailability


    public boolean checkAvailability() {
        productStorageList.clear();
        productTableViewList.clear();
        productList.clear();
        productList2.clear();

        //update tableView_products_temp on DB (one record for each row of the table)
        CustOrder sho = new CustOrder();

        sho.setNumber(txtShip_no.getText());
        sho.setCustomerId(comboCustId.getValue());
        sho.setCustomerName(txtCustName.getText());
        sho.setDate(datepicker.getValue());

        for (int i = 0; i < tableView.getItems().size(); i++) {
            OrdItems ori = new OrdItems();
            ori.setShipOrder_no(Integer.parseInt(txtShip_no.getText()));
            System.out.println(Integer.parseInt(txtShip_no.getText()));

            ori.setProductName(productNameColumn.getCellData(i));
            System.out.println(productNameColumn.getCellData(i));

            ori.setDescription(descriptionColumn.getCellData(i));
            System.out.println(descriptionColumn.getCellData(i));

            ori.setItems(itemsColumn.getCellData(i));
            System.out.println(itemsColumn.getCellData(i));

            ori.setUnitCost(costColumn.getCellData(i));
            System.out.println(costColumn.getCellData(i));

            ori.setProductTax(taxColumn.getCellData(i));
            System.out.println(taxColumn.getCellData(i));

            ori.setDiscount(discountColumn.getCellData(i));
            System.out.println((discountColumn.getCellData(i)));

            ori.setFinalCost(totalCostColumn.getCellData(i));
            System.out.println((totalCostColumn.getCellData(i)));



            try {
                String insQuery = "INSERT INTO tableView_products_temp" +
                        " (ship_no, productname, pieces, unitCost, tax, discount, finalCost)" +
                        " VALUES(?,?,?,?,?,?,?)";

                PreparedStatement insStmt = connection.prepareStatement(insQuery);
                insStmt.setInt(1, Integer.parseInt(sho.getNumber()));
                insStmt.setString(2, ori.getProductName());
                insStmt.setInt(3, ori.getItems());
                insStmt.setDouble(4, ori.getUnitCost());
                insStmt.setDouble(5, ori.getProductTax());
                insStmt.setInt(6, ori.getDiscount());
                insStmt.setDouble(7, ori.getFinalCost());
                insStmt.executeUpdate();

                insStmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        //search in the storage area and find the values of the items listing in the tableview (previous values)
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT r.productname,  p.productdescription, s.pieces, p.sup_id \n" +
                    "         FROM storage_area s JOIN products p JOIN tableView_products_temp r  INNER JOIN \n" +
                    "                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime" +
                    "         WHERE  r.productname = p.productname AND s.productname = r.productname AND r.ship_no = '" + txtShip_no.getText() + "'" +
                    "        ORDER BY productName");

            while (resultSet.next()) {

                Stock st = new Stock(
                        resultSet.getString("productName"),
                        resultSet.getString("productdescription"),
                        resultSet.getInt("pieces"),
                        resultSet.getString("sup_id")
                );

                productStorageList.add(st.getItems());
                productList.add(st.getProductName());
                System.out.println("=========================");


            }
            for(int i=0; i<productStorageList.size(); i++){
                System.out.println("previous item" + i + ":" + productStorageList.get(i));
                System.out.println("previous product" + i + ":" + productList.get(i));
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //search into the tableView_products_temp and find the values of the items of the current tableview (current values)
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT r.productname, p.productdescription, r.pieces, p.sup_id \n" +
                    "         FROM tableView_products_temp r JOIN products p  INNER JOIN \n" +
                    "                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime " +
                    "         WHERE  r.productname = p.productname AND r.ship_no = '" + txtShip_no.getText() + "'" +
                    "         ORDER BY r.productName");

            while (resultSet.next()) {

                Stock st = new Stock(
                        resultSet.getString("productName"),
                        resultSet.getString("productdescription"),
                        resultSet.getInt("pieces"),
                        resultSet.getString("sup_id")
                );


                productTableViewList.add(st.getItems());
                productList2.add(st.getProductName());
                System.out.println("=========================");

            }

            for(int i=0; i<productTableViewList.size(); i++){
                System.out.println("new item" + i + ":" + productTableViewList.get(i));
                System.out.println("new product" + i + ":" + productList2.get(i));
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //check whether items required exceed items stored
        StringBuilder exceeds = new StringBuilder();
        System.out.println("exceeds: " + exceeds);
        for (int i = 0; i < productTableViewList.size(); i++) {
            System.out.println("productStorageList.get(i): " + productStorageList.get(i));
            System.out.println("productTableViewList.get(i): " + productTableViewList.get(i));
            System.out.println("productList.get(i): " + productList.get(i));

            if (productStorageList.get(i) < productTableViewList.get(i)) {
                exceeds.append("Product ").append(productList.get(i)).append(": only ").append(productStorageList.get(i)).append(" items left in stock!\n");
                System.out.println("exceeds: " + exceeds);
            }

        }
        System.out.println("exceeds: " + exceeds);

        if(exceeds.length()>0 ){
            MessageBoxOK mb = new MessageBoxOK(exceeds.toString(), "WARNING!");
            System.out.println(exceeds);

            //delete temporary data from tableView_products_temp
            try {
                String delQuery = "DELETE FROM tableView_products_temp " +
                        " WHERE ship_no = '" + txtShip_no.getText() + "'";

                PreparedStatement statement = connection.prepareStatement(delQuery);
                statement.executeUpdate();

                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return false;

        } else {
            System.out.println("All items checked!");

            try {
                String delQuery = "DELETE FROM tableView_products_temp " +
                        " WHERE ship_no = '" + txtShip_no.getText() + "'";

                PreparedStatement statement = connection.prepareStatement(delQuery);
                statement.executeUpdate();

                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return true;
        }
    }
}