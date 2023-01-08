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
import javafx.util.converter.IntegerStringConverter;

import java.sql.*;
import java.time.LocalDate;


public class EditSupOrderForm {
    Stage stage;
    Label lblRec_no, lblDate, lblSup_id, lblSupName, lblTotalItems;
    TextField txtRec_no, txtSupName, txtDescription, txtQuantity, txtTotalItems;
    DatePicker datepicker;
    ComboBox<String> comboSupId;
    ComboBox<String> txtProductName;
    Button btnAddProduct, btnDeleteLine, btnUpdate, btnCancel;
    HBox hboxInfo, hboxProduct, hboxItems, hBoxSelect;
    TableView<SupItems> tableView;
    int total = 0;

    TableColumn<SupItems, String> productNameColumn;
    TableColumn<SupItems, String> descriptionColumn;
    TableColumn<SupItems, Integer> itemsColumn;

    String resultSupId, resultProductName;
    Connection connection;


    ObservableList<SupItems>  forStorage = FXCollections.observableArrayList();
    ObservableList<SupItems>  supItemsList = FXCollections.observableArrayList();


    ObservableList<Integer>  productStorageList = FXCollections.observableArrayList();
    ObservableList<Integer>  productTableViewList = FXCollections.observableArrayList();
    ObservableList<String>  productList = FXCollections.observableArrayList();
    ObservableList<String>  productList2 = FXCollections.observableArrayList();

    public EditSupOrderForm(Connection conn, String state, int rec_no, LocalDate rec_date, String supId, String supplierName) {

        connection = conn;

        SupOrder sor = new SupOrder(rec_no, supId, supplierName, rec_date);

        //grid
        GridPane grid = new GridPane();
        grid.setHgap(7);
        grid.setVgap(7);


        //rec_no
        lblRec_no = new Label("No: ");
        txtRec_no = new TextField();
        txtRec_no.setPrefWidth(70);
        txtRec_no.setEditable(false);

        //date
        lblDate = new Label("Date: ");
        datepicker = new DatePicker();

        //supId
        lblSup_id = new Label("Supplier Id: ");
        comboSupId = new ComboBox<String>();
        comboSupId.setEditable(true);
        new AutoCompleteComboBoxListener<>(comboSupId);
        comboSupId.setPrefWidth(70);

        //supName
        lblSupName = new Label("Supplier Name: ");
        txtSupName = new TextField();
        txtSupName.setPrefWidth(200);


        hboxInfo = new HBox();
        hboxInfo.getChildren().addAll(lblRec_no, txtRec_no, lblDate, datepicker, lblSup_id, comboSupId, lblSupName, txtSupName);
        hboxInfo.setSpacing(30);
        hboxInfo.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(hboxInfo, 7);
        grid.add(hboxInfo, 0, 0);


        //TextFields for adding the products
        txtProductName = new ComboBox<String>();
        txtProductName.setEditable(true);
        new AutoCompleteComboBoxListener<>(txtProductName);
        txtProductName.setPromptText("enter product name");

        txtDescription = new TextField();
        txtDescription.setPromptText("product description");
        txtDescription.setPrefWidth(250);

        txtQuantity = new TextField();
        txtQuantity.setPromptText("enter quantity");

        btnAddProduct = new Button("Add Product");
        btnDeleteLine = new Button("Delete Selected Line(s)");


        hboxProduct = new HBox();
        hboxProduct.getChildren().addAll(txtProductName, txtDescription, txtQuantity, btnAddProduct, btnDeleteLine);
        hboxProduct.setSpacing(20);
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


        //tableView
        productNameColumn = new TableColumn<>("Product Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.setMinWidth(307);

        descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setMinWidth(650);

        itemsColumn = new TableColumn<>("Items");
        itemsColumn.setCellValueFactory(new PropertyValueFactory<>("items"));
        itemsColumn.setMinWidth(148);


        tableView = new TableView<>();
        tableView.getColumns().add(productNameColumn);
        tableView.getColumns().add(descriptionColumn);
        tableView.getColumns().add(itemsColumn);


        productNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        itemsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        productNameColumn.setEditable(false);
        descriptionColumn.setEditable(false);


        itemsColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<SupItems, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<SupItems, Integer> supItemsIntegerCellEditEvent) {
                SupItems cellItems = tableView.getSelectionModel().getSelectedItem();
                cellItems.setItems(supItemsIntegerCellEditEvent.getNewValue());
                for (int i=0; i<tableView.getItems().size(); i++){
                    total   += itemsColumn.getCellData(i);
                    System.out.println("items " + total);
                }
                System.out.println("total items: " + total);
                txtTotalItems.setText(String.valueOf(total));
                total = 0;
            }
        });

        tableView.setPrefWidth(500);
        tableView.setPrefHeight(700);
        tableView.setEditable(true);


        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        grid.add(tableView, 1, 3, 3, 3);
        GridPane.setRowSpan(tableView, grid.getRowCount());
        GridPane.setHalignment(tableView, HPos.RIGHT);


        //Total Items
        lblTotalItems =  new Label("Total Items:");
        txtTotalItems = new TextField();
        txtTotalItems.setPrefWidth(145);

        hboxItems = new HBox();
        hboxItems.getChildren().addAll( lblTotalItems, txtTotalItems);
        hboxItems.setSpacing(10);
        hboxItems.setPadding(new Insets(5,222,0,0));
        hboxItems.setAlignment(Pos.CENTER_RIGHT);


        //cancel button
        btnCancel = new Button("Cancel");


        //update button
        btnUpdate = new Button("Update");


        hBoxSelect = new HBox();
        hBoxSelect.getChildren().addAll(btnCancel, btnUpdate);
        hBoxSelect.setAlignment(Pos.CENTER);
        hBoxSelect.setSpacing(50);
        hBoxSelect.setMinHeight(70);

        VBox pane = new VBox();
        pane.getChildren().addAll(grid, hboxItems, hBoxSelect);

        //move cursor with enter key
        txtRec_no.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                datepicker.requestFocus();
            }
        });
        datepicker.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                comboSupId.requestFocus();
            }
        });
        comboSupId.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtSupName.requestFocus();
            }
        });
        txtSupName.setOnKeyPressed( evt ->{
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
        stage.setTitle("View Supplier Order");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 700) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 600) / 2);
        stage.setWidth(700);
        stage.setHeight(600);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);
        stage.show();

        ///////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////

        checkState(state, sor);
        loadForm(sor);
        loadTableView(rec_no);


        //fill the combobox - supplier id and fill the supplier name automatically
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM supplier ORDER BY sup_id");

            //add an empty item just for the "no-choice"
            comboSupId.getItems().add("");
            while (resultSet.next()) {


                Supplier s = new Supplier(
                        resultSet.getString("sup_id"),
                        resultSet.getString("company_name"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email")
                );

                comboSupId.getItems().add(s.getSup_id());


                comboSupId.setOnAction(e -> {
                    resultSupId = comboSupId.getValue();
                    System.out.println(resultSupId);
                    try {
                        CallableStatement callstmt = conn.prepareCall("call input(?)");
                        callstmt.setString(1, resultSupId);
                        //System.out.println(resultSupId);

                        ResultSet newresult = callstmt.executeQuery();

                        while(newresult.next()){
                            System.out.println(newresult.getString("company_name"));
                            txtSupName.setText(newresult.getString("company_name"));
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
                        CallableStatement callstmt = conn.prepareCall("call fillDescription(?)");
                        callstmt.setString(1, resultProductName);

                        ResultSet newresult = callstmt.executeQuery();

                        while(newresult.next()){
                            System.out.println(newresult.getString("productdescription"));
                            txtDescription.setText(newresult.getString("productdescription"));
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

            SupItems productLine = new SupItems(txtProductName.getValue(), txtDescription.getText(), Integer.parseInt(txtQuantity.getText()));
            tableView.getItems().add(productLine);
            txtProductName.setValue("");
            txtQuantity.clear();
            txtDescription.clear();
            for (int i=0; i<tableView.getItems().size(); i++){
                total   += itemsColumn.getCellData(i);
                System.out.println("items " + total);
            }
            System.out.println("total items: " + total);
            txtTotalItems.setText(String.valueOf(total));
            total = 0;

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
            for (int i=0; i<tableView.getItems().size(); i++){
                total   += itemsColumn.getCellData(i);
                System.out.println("items " + total);
            }
            System.out.println("total items: " + total);
            txtTotalItems.setText(String.valueOf(total));
            total = 0;
        });


        //cancel button
        btnCancel.setOnAction(e->{

            checkState(state, sor);
            loadForm(sor);
            tableView.getItems().clear();
            loadTableView(rec_no);

        });

        //update button
        btnUpdate.setOnAction(e-> {
            MessageBoxCancelYes m = new MessageBoxCancelYes("Are you sure you want to make changes?","Warning!");
            boolean response = m.getResponse();
            if (response) {


                //check if all fields are filled in
                if (datepicker.getValue() == null) {
                    MessageBoxOK mb = new MessageBoxOK("Please pick a Date.", "WARNING!");
                    System.out.println("Please pick a Date.");
                } else if (comboSupId.getValue() == null) {
                    MessageBoxOK mb = new MessageBoxOK("Please choose Supplier Id.", "WARNING!");
                    System.out.println("Please choose Supplier Id.");
                } else if (tableView.getItems().size() == 0) {
                    MessageBoxOK mb = new MessageBoxOK("Table is empty! Please add products.", "WARNING!");
                    System.out.println("Table is empty! Please add products.");
                }
                //everything is ok! add data to DB
                else {
                    //1st step: delete previous data from the DB (tables: storage_area, received_items, receiving_orders)
                    //remove previous data from the storage_area
                     Statement stmt = null;
                        try {
                            stmt = conn.createStatement();
                            ResultSet resultSet = stmt.executeQuery("SELECT r.productName, p.productDescription, r.pieces " +
                                    "FROM received_items r JOIN products p " +
                                    "WHERE r.productName = p.productName AND r.rec_no = '" + txtRec_no.getText() + "' " +
                                    "ORDER BY productName");


                            //check if table is empty
                            if (!resultSet.next()) {
                                System.out.println("Table is empty");

                            } else {
                                do {
                                  SupItems si = new SupItems(

                                            resultSet.getString("productName"),
                                            resultSet.getString("productDescription"),
                                            resultSet.getInt("pieces")
                                    );
                                    forStorage.add(si);
                                } while (resultSet.next());
                            }
                            resultSet.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        for (int i=0; i<forStorage.size(); i++){

                        try {
                            String updQuery = "UPDATE storage_area" +
                                    " SET pieces = pieces - ? " +
                                    " WHERE productName = ? ";

                            PreparedStatement updStmt = connection.prepareStatement(updQuery);
                            updStmt.setInt(1, (forStorage.get(i).getItems()));
                            updStmt.setString(2, forStorage.get(i).getProductName());
                            updStmt.executeUpdate();
                            System.out.println("---------------------");

                            updStmt.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }

                    //remove previous data from received_items
                    try {
                        String delQuery2 = "DELETE FROM received_items " +
                                " WHERE rec_no = '" + rec_no + "'";

                        PreparedStatement statement = conn.prepareStatement(delQuery2);
                        statement.executeUpdate();

                        statement.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    //remove previous data from receiving_orders
                    try {
                        String delQuery = "DELETE FROM receiving_orders " +
                                " WHERE rec_no = '" + rec_no + "'";

                        PreparedStatement statement = conn.prepareStatement(delQuery);
                        statement.executeUpdate();

                        statement.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                    //2nd step: update tables with new data(receiving_orders, received_items, storage_aea)
                    //update receiving_orders table on the DB with the new data
                    SupOrder so = new SupOrder();

                    so.setNumber(txtRec_no.getText());
                    so.setSupplierId(comboSupId.getValue());
                    so.setSupplierName(txtSupName.getText());
                    so.setDate(datepicker.getValue());

                    try {
                        String insQuery = "INSERT INTO receiving_orders" +
                                " (rec_no, sup_id, supplier_name, rec_date)" +
                                " VALUES(?,?,?,?)";

                        PreparedStatement insStmt = conn.prepareStatement(insQuery);
                        insStmt.setInt(1, Integer.parseInt(so.getNumber()));
                        insStmt.setString(2, so.getSupplierId());
                        insStmt.setString(3, so.getSupplierName());
                        insStmt.setObject(4, so.getDate());
                        insStmt.executeUpdate();

                        insStmt.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                    //update received_items on DB with the new data(one record for each row of the table)
                    for (int i = 0; i < tableView.getItems().size(); i++) {
                        SupItems si = new SupItems();
                        si.setSupOrder_no(Integer.parseInt(txtRec_no.getText()));
                        System.out.println(Integer.parseInt(txtRec_no.getText()));
                        si.setProductName(productNameColumn.getCellData(i));
                        System.out.println(productNameColumn.getCellData(i));
                        si.setDescription(descriptionColumn.getCellData(i));
                        System.out.println(descriptionColumn.getCellData(i));
                        si.setItems(itemsColumn.getCellData(i));
                        System.out.println(itemsColumn.getCellData(i));

                        try {
                            String insQuery = "INSERT INTO received_items" +
                                    " (rec_no, productname, pieces)" +
                                    " VALUES(?,?,?)";

                            PreparedStatement insStmt = conn.prepareStatement(insQuery);
                            insStmt.setInt(1, Integer.parseInt(so.getNumber()));
                            insStmt.setString(2, si.getProductName());
                            insStmt.setInt(3, si.getItems());
                            insStmt.executeUpdate();

                            insStmt.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    }

                    updateStorage();

                    MessageBoxOK mb = new MessageBoxOK("Order updated successfully.", "INFORMATION!");
                    stage.close();
                } //else
            }
            else {  //response is no
                checkState(state, sor);
                loadForm(sor);
                tableView.getItems().clear();
                loadTableView(rec_no);
                }
        });
    }


    private void checkState(String state, SupOrder sor) {
        if (state.equals("View")) {
            System.out.println("View State");
            txtRec_no.setEditable(false);
            datepicker.setEditable(false);
            comboSupId.setEditable(false);
            txtSupName.setEditable(false);
            txtProductName.setVisible(false);
            txtDescription.setVisible(false);
            txtQuantity.setVisible(false);
            tableView.setEditable(true);
            productNameColumn.setEditable(false);
            descriptionColumn.setEditable(false);
            itemsColumn.setEditable(false);
            btnCancel.setVisible(false);
            btnUpdate.setVisible(false);
            btnAddProduct.setVisible(false);
            btnDeleteLine.setVisible(false);

        } else if (state.equals("Edit")) {
            System.out.println("Edit State");
            txtRec_no.setEditable(false);
            datepicker.setEditable(true);
            comboSupId.setEditable(true);
            txtSupName.setEditable(false);
            txtProductName.setVisible(true);
            txtDescription.setVisible(true);
            txtQuantity.setVisible(true);
            tableView.setEditable(true);
            productNameColumn.setEditable(false);
            descriptionColumn.setEditable(false);
            itemsColumn.setEditable(true);
            btnCancel.setVisible(true);
            btnUpdate.setVisible(true);
            btnAddProduct.setVisible(true);
            btnDeleteLine.setVisible(true);
            stage.setTitle("Edit Supplier Order");
        }
    }

    public void loadForm(SupOrder sor) {
        txtRec_no.setText(sor.getNumber());
        datepicker.setValue(sor.getDate());
        comboSupId.setValue(sor.getSupplierId());
        txtSupName.setText(sor.getSupplierName());
    }

    public void loadTableView(int rec_no) {
        Statement stmt = null;
        SupOrder sor = new SupOrder();
        try {
            CallableStatement callstmt = connection.prepareCall("call loadSupItems(?)");
            callstmt.setInt(1, rec_no);

            ResultSet newresult = callstmt.executeQuery();

            while(newresult.next()){
                supItemsList.add(new SupItems(
                        newresult.getString("productName"),
                        newresult.getString("productDescription"),
                        newresult.getInt("pieces")));
                tableView.setItems(supItemsList);

                //for the storage_area

            }
            newresult.close();
            callstmt.close();
        } catch (SQLException d) {
            d.printStackTrace();
        }
        //count total items
        for (int i=0; i<tableView.getItems().size(); i++){
            total   += itemsColumn.getCellData(i);
            System.out.println("items " + total);
        }
        System.out.println("total items: " + total);
        txtTotalItems.setText(String.valueOf(total));
        total = 0;
    }

    public void updateStorage(){
        //update storage area on DB (one record for each row of the table)

        //search in the storage area and find the values of the items listing in the tableview (previous values)
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT r.productname, p.productdescription, s.pieces, s.sup_id \n" +
                    "         FROM storage_area s JOIN products p JOIN received_items r  INNER JOIN \n" +
                    "                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime \n" +
                    "         WHERE  r.productname = p.productname AND s.productname = r.productname AND r.rec_no = '" + txtRec_no.getText() + "'" +
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

        //search into the received_items and find the values of the items of the current tableview (current values)
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT r.productname, p.productdescription, r.pieces, p.sup_id \n" +
                    "         FROM received_items r JOIN products p  INNER JOIN \n" +
                    "                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime \n" +
                    "         WHERE  r.productname = p.productname AND r.rec_no = '" + txtRec_no.getText() + "'" +
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


        //for each row of the tableView
        for (int i = 0; i < tableView.getItems().size(); i++) {
            try {
                String updQuery = "UPDATE storage_area" +
                        " SET pieces = pieces + ? " +
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
    }
}