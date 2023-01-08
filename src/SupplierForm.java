import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import java.sql.*;

public class SupplierForm {
    Stage stage;
    Label lblId, lblName, lblAddress, lblPhoneNumber, lblEmail, lblTitle;
    TextField txtId, txtName, txtAddress, txtPhoneNumber, txtEmail;
    //ComboBox<Product> comProduct;
    ListView<Supplier> listSuppliers;
    Button btnNew, btnEdit, btnDelete;
    Button btnSave, btnCancel;
    HBox hBoxSaveCancel;
    String state;

    Connection connection;

    public SupplierForm(Connection conn) {


        // the grid
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
        lblId = new Label("Id: ");
        txtId = new TextField();

        grid.add(lblId, 1, 5);
        GridPane.setHalignment(lblId, HPos.RIGHT);
        GridPane.setValignment(lblId, VPos.CENTER);

        grid.add(txtId, 2, 5);
        GridPane.setHalignment(txtId, HPos.LEFT);
        GridPane.setValignment(txtId, VPos.CENTER);

        //name
        lblName = new Label("Name: ");
        txtName = new TextField();

        grid.add(lblName, 1, 6);
        GridPane.setHalignment(lblName, HPos.RIGHT);
        GridPane.setValignment(lblName, VPos.CENTER);

        grid.add(txtName, 2, 6);
        GridPane.setHalignment(txtName, HPos.LEFT);
        GridPane.setValignment(txtName, VPos.CENTER);

        //address
        lblAddress = new Label("Address: ");
        txtAddress = new TextField();

        grid.add(lblAddress, 1, 7);
        GridPane.setHalignment(lblAddress, HPos.RIGHT);
        GridPane.setValignment(lblAddress, VPos.CENTER);

        grid.add(txtAddress, 2, 7);
        GridPane.setHalignment(txtAddress, HPos.LEFT);
        GridPane.setValignment(txtAddress, VPos.CENTER);

        //phoneNumber
        lblPhoneNumber = new Label("Phone Number: ");
        txtPhoneNumber = new TextField();

        grid.add(lblPhoneNumber, 1, 8);
        GridPane.setHalignment(lblPhoneNumber, HPos.RIGHT);
        GridPane.setValignment(lblPhoneNumber, VPos.CENTER);

        grid.add(txtPhoneNumber, 2, 8);
        GridPane.setHalignment(txtPhoneNumber, HPos.LEFT);
        GridPane.setValignment(txtPhoneNumber, VPos.CENTER);

        //email
        lblEmail = new Label("Email: ");
        txtEmail = new TextField();

        grid.add(lblEmail, 1, 9);
        GridPane.setHalignment(lblEmail, HPos.RIGHT);
        GridPane.setValignment(lblEmail, VPos.CENTER);

        grid.add(txtEmail, 2, 9);
        GridPane.setHalignment(txtEmail, HPos.LEFT);
        GridPane.setValignment(txtEmail, VPos.CENTER);


        //buttons
        btnSave = new Button("Save");
        btnSave.setOnAction(e -> {
            if (listSuppliers.getItems().size()==0 ){
                System.out.println("Nothing to save...");
            }
            if (validateForm()) {
                System.out.println("Validate: OK");
                if (state.equals("EDIT")) {

                    //String oldValue = listSuppliers.getSelectionModel().getSelectedItem().getSup_Id();

                    Supplier s = listSuppliers.getSelectionModel().getSelectedItem();

                    String oldValue = s.getSup_id();

                    s.setSup_id(txtId.getText());
                    s.setName(txtName.getText());
                    s.setAddress(txtAddress.getText().equals("") ? null : txtAddress.getText());
                    s.setPhoneNumber(txtPhoneNumber.getText());
                    s.setEmail(txtEmail.getText().equals("") ? null : txtEmail.getText());


                    try {
                        String updQuery = "UPDATE supplier" +
                                " SET sup_id = ?, company_name = ?, address = ?, " +
                                "     phonenumber = ?, email = ? " +
                                " WHERE sup_id = ? ";

                        PreparedStatement updStmt = conn.prepareStatement(updQuery);
                        updStmt.setString(1, s.getSup_id());
                        updStmt.setString(2, s.getName());
                        updStmt.setObject(3, s.getAddress());
                        updStmt.setString(4, s.getPhoneNumber());
                        updStmt.setObject(5, s.getEmail());
                        updStmt.setString(6, oldValue);
                        updStmt.executeUpdate();
                        System.out.println("---------------------");
                        System.out.println("Old Value (sup_id):" + oldValue);

                        updStmt.close();
                    } catch (SQLException throwables) {
                        //throwables.printStackTrace();
                        MessageBoxOK mb = new MessageBoxOK("You cannot edit the supplier's id", "WARNING!");
                        System.out.println("You cannot edit the supplier's id");
                    }


                    state = "VIEW";
                    changeState(state, s);


                } else if (state.equals("ADD")) {

                    Supplier s = new Supplier();
                    s.setSup_id(txtId.getText());
                    s.setName(txtName.getText());
                    s.setAddress(txtAddress.getText().equals("")?null: txtAddress.getText());
                    s.setPhoneNumber(txtPhoneNumber.getText());
                    s.setEmail(txtEmail.getText().equals("")?null: txtEmail.getText());

                    try {
                        String insQuery = "INSERT INTO supplier" +
                                " (sup_id, company_name, address, phonenumber, email)" +
                                " VALUES(?,?,?,?,?)" ;

                        PreparedStatement insStmt = conn.prepareStatement(insQuery);
                        insStmt.setString(1, s.getSup_id());
                        insStmt.setString(2, s.getName());
                        insStmt.setObject(3, s.getAddress());
                        insStmt.setString(4, s.getPhoneNumber());
                        insStmt.setObject(5, s.getEmail());
                        insStmt.executeUpdate();

                        insStmt.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                    state = "VIEW";
                    listSuppliers.getItems().add(s);
                    listSuppliers.getSelectionModel().select(s);
                    changeState(state, s);


                }
            }
        });
        btnCancel = new Button("Cancel");
        btnCancel.setOnAction(e -> {
            state = "VIEW";
            if (listSuppliers.getItems().size() ==0){
                System.out.println("No action");
                changeState(state, new Supplier());

            }else {
                changeState(state, listSuppliers.getSelectionModel().getSelectedItem());
            }
        });
        hBoxSaveCancel = new HBox();
        hBoxSaveCancel.getChildren().addAll(btnSave, btnCancel);
        hBoxSaveCancel.setSpacing(10);
        hBoxSaveCancel.setAlignment(Pos.CENTER);
        grid.add(hBoxSaveCancel, 1, 15);
        GridPane.setRowSpan(hBoxSaveCancel, 2);


        // the listview
        listSuppliers = new ListView<>();
        loadDB(conn);
        if(listSuppliers.getItems().size()==0){
            System.out.println("Empty list");
            state = "VIEW";

        } else{
            listSuppliers.getSelectionModel().selectedItemProperty().
                    addListener((observable, oldValue, newValue) -> {
                        load_form(newValue);
                        System.out.println("oldValue: " +oldValue);
                        System.out.println("newValue: " +newValue);
                    });
        }


        grid.add(listSuppliers, 0, 0);
        GridPane.setRowSpan(listSuppliers, grid.getRowCount() + 1);
        GridPane.setHalignment(listSuppliers, HPos.LEFT);


        // buttons
        btnNew = new Button();
        btnNew.setGraphic(new ImageView(new Image("add-icon.png")));
        btnNew.setPrefWidth(listSuppliers.getWidth() / 3 - 3);
        btnNew.setOnAction(e -> {
            state = "ADD";
            changeState(state, null);
        });
        btnEdit = new Button();
        btnEdit.setGraphic(new ImageView(new Image("edit_icon.png")));
        btnEdit.setPrefWidth(listSuppliers.getWidth() / 3 - 3);
        btnEdit.setOnAction(e -> {
            if (listSuppliers.getItems().size()==0){
                System.out.println("Nothing to edit...");
            } else {
                state = "EDIT";
                changeState(state, listSuppliers.getSelectionModel().getSelectedItem());
            }
        });
        btnDelete = new Button();
        btnDelete.setGraphic(new ImageView(new Image("delete_icon.png")));
        btnDelete.setPrefWidth(listSuppliers.getWidth() / 3 - 3);
        btnDelete.setOnAction(e -> {
            if (listSuppliers.getItems().size()==0) {
                System.out.println("The list is empty. No data to delete...");
                state = "VIEW";
                changeState(state, new Supplier());
            }else {
                MessageBoxCancelYes m = new MessageBoxCancelYes("Are you sure you want \n to delete the supplier?", "Warning!");
                boolean response = m.getResponse();
                if (response) {
                    Supplier s = listSuppliers.getSelectionModel().getSelectedItem();
                    listSuppliers.getItems().remove(s);
                    if (listSuppliers.getItems().size() > 0)
                        listSuppliers.getSelectionModel().select(0);
                    else {
                        state = "ADD";
                        changeState(state, null);
                    }
                    try {
                        String delQuery = "DELETE FROM supplier " +
                                " WHERE sup_id = '" + s.getSup_id() + "'";

                        PreparedStatement statement = conn.prepareStatement(delQuery);
                        statement.executeUpdate();

                        statement.close();
                    } catch (SQLException throwables) {
                        //throwables.printStackTrace();
                        MessageBoxOK mb = new MessageBoxOK("You cannot delete an active supplier!", "WARNING!");
                        System.out.println("You cannot delete an active supplier!");
                    }
                }
            }//else
        });

        //move cursor with enter key
        txtId.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtName.requestFocus();
            }
        });
        txtName.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtAddress.requestFocus();
            }
        });
        txtAddress.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtPhoneNumber.requestFocus();
            }
        });
        txtPhoneNumber.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtEmail.requestFocus();
            }
        });

        HBox hbox = new HBox();
        hbox.getChildren().addAll(btnNew, btnEdit, btnDelete);
        hbox.setSpacing(25);
        hbox.setAlignment(Pos.TOP_LEFT);
        grid.add(hbox, 2, 0);


        //grid constraints
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
                    listSuppliers.setPrefHeight(scene.getHeight());
                });
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> {
                    grid.setPrefWidth(scene.getWidth());
                });

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Supplier");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 700) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 600) / 2);
        stage.setWidth(700);
        stage.setHeight(600);
        stage.initModality(Modality.APPLICATION_MODAL);



        if (listSuppliers.getItems().size()!=0) {
            listSuppliers.getSelectionModel().select(0);
            state = "VIEW";
            changeState(state, listSuppliers.getSelectionModel().getSelectedItem());
        } else{
            System.out.println("Data empty");
            state = "VIEW";
            changeState(state, new Supplier());
        }
        stage.show();

    }

    private boolean validateForm() {
        String errors = "";
        // Id: 3 characters
        String id = txtId.getText();
        if (id.length() != 3) {
            errors += "Id must be exactly 3 characters\n";
        }
        // Name: <=30 chars and non empty
        String name = txtName.getText();
        if (name.length() > 30) {
            errors += "Name must be <= 30 chars\n";
        }
        if (name.isBlank()) {
            errors += "Name can't be empty\n";
        }

        // Address: <=30 chars
        String address = txtAddress.getText();
        if (address.length() > 30) {
            errors += "Address must be <= 30 chars\n";
        }

        // PhoneNumber: =10 chars
        String phone = txtPhoneNumber.getText();
        if (phone.length() != 10) {
            errors += "Phone must be exactly 10 chars\n";
        }

        // Email: <=50 chars
        String email = txtEmail.getText();
        if (email.length() > 50) {
            errors += "Email must be <= 50 chars\n";
        }

        if (errors.length() > 0) {
            MessageBoxOK mb = new MessageBoxOK(errors, "ERRORS");
            return false;
        } else {
            String output =
                    "id " + id + "\n" +
                            "name: " + name + "\n" +
                            "address: " + address + "\n" +
                            "phonenumber: " + phone + "\n" +
                            "email: " + email + "\n";
            System.out.println(output);
            return true;
        }
    }

    public void load_form(Supplier s) {
        txtId.setText(s.getSup_id());
        txtName.setText(s.getName());
        txtAddress.setText(s.getAddress() == null ? "" : s.getAddress());
        txtPhoneNumber.setText(s.getPhoneNumber());
        txtEmail.setText(s.getEmail() == null ? "" : s.getEmail());
    }


        public void save_form(Supplier s){

            s.setSup_id(txtId.getText());
            s.setName(txtName.getText());
            s.setAddress(txtAddress.getText().equals("") ? null : txtAddress.getText());
            s.setPhoneNumber(txtPhoneNumber.getText());
            s.setEmail(txtEmail.getText().equals("") ? null : txtEmail.getText());

        }


        public void changeState(String state, Supplier s){
            switch (state) {
                case "ADD":
                    lblTitle.setText("Add Supplier");

                    txtId.setEditable(true);
                    txtName.setEditable(true);
                    txtAddress.setEditable(true);
                    txtPhoneNumber.setEditable(true);
                    txtEmail.setEditable(true);

                    txtId.setText("");
                    txtName.setText("");
                    txtAddress.setText("");
                    txtPhoneNumber.setText("");
                    txtEmail.setText("");

                    hBoxSaveCancel.setVisible(true);
                    btnNew.setDisable(true);
                    btnEdit.setDisable(true);
                    btnDelete.setDisable(true);
                    break;

                case "EDIT":
                    lblTitle.setText("Edit Supplier");

                    txtId.setEditable(true);
                    txtName.setEditable(true);
                    txtAddress.setEditable(true);
                    txtPhoneNumber.setEditable(true);
                    txtEmail.setEditable(true);

                    load_form(s);
                    hBoxSaveCancel.setVisible(true);
                    btnNew.setDisable(true);
                    btnEdit.setDisable(true);
                    btnDelete.setDisable(true);
                    break;

                case "VIEW":
                    lblTitle.setText("View Supplier");

                    txtId.setEditable(false);
                    txtName.setEditable(false);
                    txtAddress.setEditable(false);
                    txtPhoneNumber.setEditable(false);
                    txtEmail.setEditable(false);

                    load_form(s);
                    hBoxSaveCancel.setVisible(false);
                    btnNew.setDisable(false);
                    btnEdit.setDisable(false);
                    btnDelete.setDisable(false);

            }
        }


    public void loadDB(Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM supplier" +
                    " ORDER BY company_name");

            //check if table is empty
            if (!resultSet.next()) {
                System.out.println("Table is empty");

            } else {

                do {
                    Supplier s = new Supplier(
                            resultSet.getString("sup_id"),
                            resultSet.getString("company_name"),
                            (String) resultSet.getObject("address"),  //in case it's null
                            resultSet.getString("phonenumber"),
                            (String) resultSet.getObject("email")  //in case it's null
                    );
                    listSuppliers.getItems().add(s);


                } while (resultSet.next());
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

