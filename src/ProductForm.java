import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.*;
import java.text.DecimalFormat;


public class ProductForm {
    Stage stage;
    Label lblId, lblName, lblDescription, lblTitle, lblUnitCost, lblTax, lblTaxCombo, lblTime;
    TextField  txtName, txtDescription, txtUnitCost, txtTax, txtTime;
    ComboBox<String> comboTax;
    ComboBox<String> txtIdcombo;
    ListView<Product> productList;
    Button btnNew, btnEdit, btnDelete;
    Button btnSave, btnCancel;
    HBox hBoxSaveCancel;
    String state;
    int storageItems;


    Connection connection;

    public ProductForm(Connection conn) {
            connection = conn;

        //grid
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(5);

        //label
        lblTitle = new Label();
        lblTitle.setFont(Font.font(null, FontWeight.BOLD, 15));

        grid.add(lblTitle, 1, 3);
        GridPane.setHalignment(lblTitle, HPos.RIGHT);
        GridPane.setValignment(lblTitle, VPos.CENTER);

        //id
        lblId = new Label("Supplier's Id: ");

        txtIdcombo = new ComboBox<String>();
        txtIdcombo.setEditable(true);
        new AutoCompleteComboBoxListener<>(txtIdcombo);
        txtIdcombo.setPrefWidth(100);

        grid.add(lblId, 1, 5);
        GridPane.setHalignment(lblId, HPos.RIGHT);
        GridPane.setValignment(lblId, VPos.CENTER);

        grid.add(txtIdcombo, 2, 5);
        GridPane.setHalignment(txtIdcombo, HPos.LEFT);
        GridPane.setValignment(txtIdcombo, VPos.CENTER);

        //name
        lblName = new Label("Product Name: ");
        txtName = new TextField();

        grid.add(lblName, 1, 6);
        GridPane.setHalignment(lblName, HPos.RIGHT);
        GridPane.setValignment(lblName, VPos.CENTER);

        grid.add(txtName, 2, 6);
        GridPane.setHalignment(txtName, HPos.LEFT);
        GridPane.setValignment(txtName, VPos.CENTER);

        //description
        lblDescription = new Label("Description: ");
        txtDescription = new TextField();

        grid.add(lblDescription, 1, 7);
        GridPane.setHalignment(lblDescription, HPos.RIGHT);
        GridPane.setValignment(lblDescription, VPos.CENTER);

        grid.add(txtDescription, 2, 7);
        GridPane.setHalignment(txtDescription, HPos.LEFT);
        GridPane.setValignment(txtDescription, VPos.CENTER);

        //unitCost
        lblUnitCost = new Label("Unit Cost: ");
        txtUnitCost = new TextField();

        grid.add(lblUnitCost, 1, 8);
        GridPane.setHalignment(lblUnitCost, HPos.RIGHT);
        GridPane.setValignment(lblUnitCost, VPos.CENTER);

        grid.add(txtUnitCost, 2, 8);
        GridPane.setHalignment(txtUnitCost, HPos.LEFT);
        GridPane.setValignment(txtUnitCost, VPos.CENTER);

        //tax%
        lblTaxCombo = new Label ("Select tax(%)");
        comboTax = new ComboBox<String>();
        comboTax.setEditable(true);
        for (int i=1; i<100; i++){ comboTax.getItems().add(String.valueOf(i)); }
        new AutoCompleteComboBoxListener<>(comboTax);
        comboTax.setPrefWidth(100);

        grid.add(lblTaxCombo, 1, 9);
        GridPane.setHalignment(lblTaxCombo, HPos.RIGHT);
        GridPane.setValignment(lblTaxCombo, VPos.CENTER);

        grid.add(comboTax, 2, 9);
        GridPane.setHalignment(comboTax, HPos.LEFT);
        GridPane.setValignment(comboTax, VPos.CENTER);

        //tax
        lblTax = new Label("Tax: ");
        txtTax = new TextField();
        grid.add(lblTax, 1, 10);
        GridPane.setHalignment(lblTax, HPos.RIGHT);
        GridPane.setValignment(lblTax, VPos.CENTER);

        grid.add(txtTax, 2, 10);
        GridPane.setHalignment(txtTax, HPos.LEFT);
        GridPane.setValignment(txtTax, VPos.CENTER);

        //time changed
        lblTime = new Label("Time created/modified: ");
        txtTime= new TextField();
        grid.add(lblTime, 1, 11);
        GridPane.setHalignment(lblTime, HPos.RIGHT);
        GridPane.setValignment(lblTime, VPos.CENTER);

        grid.add(txtTime, 2, 11);
        GridPane.setHalignment(txtTime, HPos.LEFT);
        GridPane.setValignment(txtTime, VPos.CENTER);

        comboTax.setOnAction(e->{

            Double taxResult =Double.parseDouble(txtUnitCost.getText())*Double.parseDouble(comboTax.getValue())/100.0;

            DecimalFormat df = new DecimalFormat("#.00");
            Double taxResult1 = Double.parseDouble(df.format(taxResult));

            String taxResult2 = String.valueOf(taxResult1);
            txtTax.setText(taxResult2);
        });

        //in case unitcost changes
        txtUnitCost.setOnAction(e->{

            Double taxResult =Double.parseDouble(txtUnitCost.getText())*Double.parseDouble(comboTax.getValue())/100.0;

            DecimalFormat df = new DecimalFormat("#.00");
            Double taxResult1 = Double.parseDouble(df.format(taxResult));

            String taxResult2 = String.valueOf(taxResult1);
            txtTax.setText(taxResult2);
        });

        //buttons
        btnSave = new Button("Save");
        btnSave.setOnAction(e -> {
            if (productList.getItems().size()==0 ){
                System.out.println("Nothing to save...");
            }
            if (validateForm()) {
                System.out.println("Validate: OK");
                if (state.equals("EDIT")) {


                        Double taxResult =Double.parseDouble(txtUnitCost.getText())*Double.parseDouble(comboTax.getValue())/100.0;

                        DecimalFormat df = new DecimalFormat("#.00");
                        Double taxResult1 = Double.parseDouble(df.format(taxResult));

                        String taxResult2 = String.valueOf(taxResult1);
                        txtTax.setText(taxResult2);



                    Product p1 = productList.getSelectionModel().getSelectedItem();
                    Product p = new Product();
                    String oldValue = p1.getProductId();


                    String oldName = p1.getProductName();
                    String oldSupId = p1.getProductId();
                    String oldDesc = p1.getDescription();
                    String name = txtName.getText();
                    String supId = txtIdcombo.getValue();
                    String desc = txtDescription.getText();

                    if (!name.equals(oldName) | !supId.equals(oldSupId)) {
                        MessageBoxOK mb = new MessageBoxOK("Name or supplier Id can't be changed!", "WARNING!");
                        System.out.println("Name or supplier Id can't be changed!");

                    }

                     else {

                            //fill the fields
                            p.setProductId(txtIdcombo.getValue());
                            p.setProductName(txtName.getText());
                            p.setDescription(txtDescription.getText());
                            p.setUnitPrice(Double.parseDouble(txtUnitCost.getText()));
                            p.setTaxRate(comboTax.getValue());
                            p.setTax(Double.parseDouble(txtTax.getText()));
                            p.setTimeStamp();
                            System.out.println("p: " + p.getProductId() + p.getProductName() + p.getDescription() + p.getUnitPrice() + p.getTax() + p.getTimeStamp());


                            try {
                                String insQuery = "INSERT INTO products" +
                                        " (sup_id, productName, productDescription, unitCost, taxRate, tax, time_changed)" +
                                        " VALUES(?,?,?,?,?,?,?)";

                                PreparedStatement insStmt = conn.prepareStatement(insQuery);
                                insStmt.setString(1, p.getProductId());
                                insStmt.setString(2, p.getProductName());
                                insStmt.setObject(3, p.getDescription());
                                insStmt.setDouble(4, p.getUnitPrice());
                                insStmt.setString(5, p.getTaxRate());
                                insStmt.setDouble(6, p.getTax());
                                insStmt.setTimestamp(7, p.getTimeStamp());
                                insStmt.executeUpdate();

                                insStmt.close();


                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }

                            productList.getItems().clear();
                            loadDB(conn);
                            //productList.getSelectionModel().select(p);
                            state = "VIEW";
                            changeState(state, p);

                        }


                } else if (state.equals("ADD")) {

                    Product p = new Product();

                    p.setProductId(txtIdcombo.getValue());
                    p.setProductName(txtName.getText());
                    p.setDescription(txtDescription.getText());
                    p.setUnitPrice(Double.parseDouble(txtUnitCost.getText()));
                    p.setTaxRate(comboTax.getValue());
                    p.setTax(Double.parseDouble(txtTax.getText()));
                    p.setTimeStamp();
                    System.out.println("p: " + p.getProductId() + p.getProductName() + p.getDescription() + p.getUnitPrice() + p.getTax() + p.getTimeStamp());


                    try {
                        String insQuery = "INSERT INTO products" +
                                " (sup_id, productName, productDescription, unitCost, taxRate, tax, time_changed)" +
                                " VALUES(?,?,?,?,?,?,?)" ;

                        PreparedStatement insStmt = conn.prepareStatement(insQuery);
                        insStmt.setString(1, p.getProductId());
                        insStmt.setString(2, p.getProductName());
                        insStmt.setObject(3, p.getDescription());
                        insStmt.setDouble(4, p.getUnitPrice());
                        insStmt.setString(5, p.getTaxRate());
                        insStmt.setDouble(6, p.getTax());
                        insStmt.setTimestamp(7, p.getTimeStamp());
                        insStmt.executeUpdate();

                        insStmt.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    //add the product to the storage_area table

                    Stock stock = new Stock();
                    stock.setProductName(txtName.getText());
                    stock.setItems(0);
                    stock.setSupplierId(txtIdcombo.getValue());

                    try {
                        String insQuery = "INSERT INTO storage_area" +
                                " (sup_id, productName, pieces)" +
                                " VALUES(?,?,?)";

                        PreparedStatement insStmt = connection.prepareStatement(insQuery);
                        insStmt.setString(1,stock.getSupplierId());
                        insStmt.setString(2, stock.getProductName());
                        insStmt.setInt(3, stock.getItems());


                        insStmt.executeUpdate();

                        insStmt.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    //////////////////////////////////////

                    state = "VIEW";
                    productList.getItems().add(p);
                    productList.getSelectionModel().select(p);
                    changeState(state, p);


                }
            }
        });
        btnCancel = new Button("Cancel");
        btnCancel.setOnAction(e -> {
            state = "VIEW";
            if (productList.getItems().size() ==0){
                System.out.println("No action");
                changeState(state, new Product());

            }else {
                changeState(state, productList.getSelectionModel().getSelectedItem());
            }
        });
        hBoxSaveCancel = new HBox();
        hBoxSaveCancel.getChildren().addAll(btnSave, btnCancel);
        hBoxSaveCancel.setSpacing(10);
        hBoxSaveCancel.setAlignment(Pos.CENTER);
        grid.add(hBoxSaveCancel, 1, 15);
        GridPane.setRowSpan(hBoxSaveCancel, 2);


        // the listview
        productList = new ListView<>();
        loadDB(conn);

        if(productList.getItems().size()==0){
            System.out.println("Empty list");
            state = "VIEW";

        } else {
            productList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                load_form(newValue);
            });
        }

        grid.add(productList, 0, 0);
        GridPane.setRowSpan(productList, grid.getRowCount() + 1);
        GridPane.setHalignment(productList, HPos.LEFT);


        // buttons
        btnNew = new Button();
        btnNew.setGraphic(new ImageView(new Image("add-icon.png")));
        btnNew.setPrefWidth(productList.getWidth() / 3 - 3);
        btnNew.setOnAction(e -> {
            state = "ADD";
            changeState(state, null);
        });
        btnEdit = new Button();
        btnEdit.setGraphic(new ImageView(new Image("edit_icon.png")));
        btnEdit.setPrefWidth(productList.getWidth() / 3 - 3);
        btnEdit.setOnAction(e -> {
            if (productList.getItems().size()==0){
                System.out.println("Nothing to edit...");
            } else {
                state = "EDIT";
                changeState(state, productList.getSelectionModel().getSelectedItem());
            }
        });
        btnDelete = new Button();
        btnDelete.setGraphic(new ImageView(new Image("delete_icon.png")));
        btnDelete.setPrefWidth(productList.getWidth() / 3 - 3);
        btnDelete.setOnAction(e -> {
            if (productList.getItems().size()==0){
                System.out.println("The list is empty. No data to delete...");
                state = "VIEW";
            } else {
                MessageBoxCancelYes m = new MessageBoxCancelYes("Are you sure you want\nto delete the selected product?", "Warning!");
                boolean response = m.getResponse();
                if (response) {

                    //check whether the product has >0 items in the storage_area and if yes inform the user
                    Product p = productList.getSelectionModel().getSelectedItem();
                    try {
                        Statement stmt = connection.createStatement();
                        ResultSet resultSet = stmt.executeQuery("SELECT s.productname, p.productdescription, s.pieces, s.sup_id \n" +
                                "         FROM storage_area s JOIN products p\n" +
                                "         WHERE  s.productname = p.productname" +
                                "         AND p.productname = '" + p.getProductName() + "'");


                        while (resultSet.next()) {

                            Stock st = new Stock(
                                    resultSet.getString("productname"),
                                    resultSet.getString("productdescription"),
                                    resultSet.getInt("pieces"),
                                    resultSet.getString("sup_id")
                            );

                            storageItems = st.getItems();
                        }
                        resultSet.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    if (storageItems > 0) {
                        MessageBoxOK message = new MessageBoxOK("Items in storage >0 \nProduct cannot be deleted", "WARNING");

                        if (productList.getItems().size() > 0)
                            productList.getSelectionModel().select(0);
                        else {
                            state = "ADD";
                            changeState(state, new Product());
                        }
                    } else {

                        productList.getItems().remove(p);
                        if (productList.getItems().size() > 0)
                            productList.getSelectionModel().select(0);
                        else {
                            state = "ADD";
                            changeState(state, new Product());
                        }
                        try {
                            String delQuery = "DELETE FROM storage_area " +
                                    " WHERE productname = '" + p.getProductName() + "'";

                            PreparedStatement statement = conn.prepareStatement(delQuery);
                            statement.executeUpdate();

                            statement.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        try {
                            String delQuery = "DELETE FROM products " +
                                    " WHERE productname = '" + p.getProductName() + "'";

                            PreparedStatement statement = conn.prepareStatement(delQuery);
                            statement.executeUpdate();

                            statement.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
        });


        //move cursor with enter key
        txtIdcombo.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtName.requestFocus();
            }
        });
        txtName.setOnKeyPressed( evt ->{
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
                comboTax.requestFocus();
            }
        });
        comboTax.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtTax.requestFocus();
            }
        });


        HBox hbox = new HBox();
        hbox.getChildren().addAll(btnNew, btnEdit, btnDelete);
        hbox.setSpacing(25);
        hbox.setAlignment(Pos.TOP_LEFT);
        grid.add(hbox, 2, 0);



        // grid constraints
        GridPane.setColumnSpan(hBoxSaveCancel, 2);
        GridPane.setHalignment(hBoxSaveCancel, HPos.CENTER);
        grid.setPrefWidth(700);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new Insets(5));

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

        // the scene
        Scene scene = new Scene(grid);
        scene.heightProperty().addListener(
                (observable, oldValue, newValue) -> {
                    grid.setPrefHeight(scene.getHeight());
                    productList.setPrefHeight(scene.getHeight());
                });
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> {
                    grid.setPrefWidth(scene.getWidth());
                });

        //the stage
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Product");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 700) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 600) / 2);
        stage.setWidth(700);
        stage.setHeight(600);
        stage.initModality(Modality.APPLICATION_MODAL);



        if(productList.getItems().size()!=0) {
            productList.getSelectionModel().select(0);
            state = "VIEW";
            changeState(state, productList.getSelectionModel().getSelectedItem());
        } else {
            System.out.println("Data empty");
            state = "VIEW";
            changeState(state, new Product());
        }
        stage.show();

    }

    private boolean validateForm() {
        String errors = "";
        // Id: 3 characters and value should be in the list
        String id = txtIdcombo.getValue();
        if (id.length() != 3) {
            errors += "Id must be exactly 3 characters\n";
        }
        if (!txtIdcombo.getItems().contains(id)) {
            errors += "Id must be a value from the list\n";
        }

        // Name: <=5 chars
        String name = txtName.getText();
        if (name.length() != 5) {
            errors += "Name must be exactly 5 characters\n";
        }
        if  (state.equals("ADD") && productList.getItems().toString().contains(txtName.getText())) {
            errors += "Name must be unique\n";
        }
        ///////////////////////////////////////////////


        // Description: <=100 chars
        String description = txtDescription.getText();
        if (description.length() > 100) {
            errors += "Description must be <= 100 chars\n";
        }
        if (description.isBlank()) {
            errors += "Description can't be empty\n";
        }

        // UnitCost: must be double and not null
        Double unitCost = 0.0;

        try {
            unitCost = Double.valueOf(txtUnitCost.getText());
        } catch (NumberFormatException ob) {
            errors += "UnitCost must be a number\n";
        }
        if (String.valueOf(unitCost).isBlank()) {
            errors += "UnitCost can't be empty\n";
        }


        //select tax%
        String id2 = comboTax.getValue();
        if (!comboTax.getItems().contains(id2)) {
            errors += "%tax must be a value from the list\n";
        }
        if (comboTax.getValue() == null) {
            errors += "%tax can't be empty\n";
        }

        // Tax: must be double and not null
        Double tax = 0.0;
        try {
            tax = Double.valueOf(txtTax.getText());
        } catch (NumberFormatException ob) {
            errors += "Tax must be a number\n";
        }
        if (String.valueOf(tax).isBlank()) {
            errors += "Tax can't be empty\n";
        }

        if (errors.length() > 0) {
            MessageBoxOK mb = new MessageBoxOK(errors, "ERRORS");
            return false;
        } else {

            String output =
                    "supplierId " + id + "\n" +
                            "productName: " + name + "\n" +
                            "description: " + description + "\n"+
                            "unitCost: " + unitCost + "\n"+
                            "tax: " + tax + "\n"+
                            "comboTax: " + comboTax.getValue() + "\n";
            System.out.println(output);
            return true;
        }
    }

    public void load_form(Product p) {

        //fill the combobox for supplierId
       try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM supplier" +
                    " WHERE sup_id='" + p.getProductId() +"'");

           //check if table is empty
           if (!resultSet.next()) {
               System.out.println("Table is empty");
               //txtRec_no.setText("1");
           } else {

               do {
                   Supplier supplier = new Supplier(
                           resultSet.getString("sup_id"),
                           resultSet.getString("company_name"),
                           (String) resultSet.getObject("address"),  //in case it's null
                           resultSet.getString("phonenumber"),
                           (String) resultSet.getObject("email")  //in case it's null
                   );

            txtIdcombo.getSelectionModel().select(supplier.getSup_id());

               } while (resultSet.next());
           }
           resultSet.close();
       } catch (SQLException throwables) {
           throwables.printStackTrace();
       }


        txtName.setText(p.getProductName());
        txtDescription.setText(p.getDescription());
        txtUnitCost.setText(String.valueOf(p.getUnitPrice()));
        comboTax.setValue(p.getTaxRate());
        txtTax.setText(String.valueOf(p.getTax()));
        txtTime.setText(String.valueOf(p.getTimeStamp()));

    }



        public void changeState(String state, Product p){
            switch (state) {
                case "ADD":

                    lblTitle.setText("Add Product");

                    txtIdcombo.setEditable(true);
                    txtName.setEditable(true);
                    txtDescription.setEditable(true);
                    txtUnitCost.setEditable(true);
                    comboTax.setEditable(true);
                    txtTax.setEditable(true);

                    txtIdcombo.setValue("");
                    txtName.setText("");
                    txtDescription.setText("");
                    txtUnitCost.setText("");
                    comboTax.setValue("");
                    txtTax.setText("");
                    txtTime.setText("");

                    hBoxSaveCancel.setVisible(true);
                    productList.setDisable(true);
                    btnNew.setDisable(true);
                    btnEdit.setDisable(true);
                    btnDelete.setDisable(true);

                    break;

                case "EDIT":
                    lblTitle.setText("Edit Product");

                    txtIdcombo.setEditable(true);
                    txtName.setEditable(true);
                    txtDescription.setEditable(true);
                    txtUnitCost.setEditable(true);
                    comboTax.setEditable(true);
                    txtTax.setEditable(true);

                    load_form(p);
                    hBoxSaveCancel.setVisible(true);
                    productList.setDisable(true);
                    btnNew.setDisable(true);
                    btnEdit.setDisable(true);
                    btnDelete.setDisable(true);
                    break;

                case "VIEW":
                    lblTitle.setText("View Product");

                    txtIdcombo.setEditable(true);
                    txtName.setEditable(false);
                    txtDescription.setEditable(false);
                    txtUnitCost.setEditable(false);
                    comboTax.setEditable(true);
                    txtTax.setEditable(false);


                    load_form(p);
                    hBoxSaveCancel.setVisible(false);
                    productList.setDisable(false);
                    btnNew.setDisable(false);
                    btnEdit.setDisable(false);
                    btnDelete.setDisable(false);

            }
        }


    public void loadDB(Connection conn) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT p.* FROM products p INNER JOIN (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime");

            if (!resultSet.next()) {
                System.out.println("Table is empty");

            } else {

                do {
                    Product p = new Product(
                            resultSet.getString("sup_id"),
                            resultSet.getString("productname"),
                            resultSet.getString("productdescription"),
                            resultSet.getDouble("unitCost"),
                            resultSet.getString("taxRate"),
                            resultSet.getDouble("tax"),
                            resultSet.getTimestamp("time_changed")
                    );

                    productList.getItems().add(p);
                } while (resultSet.next());
            }
            resultSet.close();


            // fill the combo box
            resultSet = stmt.executeQuery("SELECT * FROM supplier" +
                    " ORDER BY sup_id");

            while(resultSet.next()) {
                Supplier s = new Supplier(
                        resultSet.getString("sup_id"),
                        resultSet.getString("company_name"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email")
                );
                txtIdcombo.getItems().add(s.getSup_id());
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

