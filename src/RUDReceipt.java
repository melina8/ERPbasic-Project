import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import java.sql.*;
import java.time.LocalDate;


public class RUDReceipt {
    Stage stage;

    ListView<CustomerReceipt> cusReceiptList;
    Button btnEdit, btnDelete, btnGo;
    Label lblDate, lblToDate;
    DatePicker datepicker1, datepicker2;
    ComboBox<String> cus_IdCombo;
    String resultCustId;

   Connection connection;

    public RUDReceipt(Connection conn) {
        connection = conn;

        //grid
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(7);


        // btnGo
        btnGo = new Button("GO");

        // btnEdit
        btnEdit = new Button("EDIT RECEIPT");

        // btnDelete
        btnDelete = new Button("DELETE RECEIPT");


        // the listview
        cusReceiptList = new ListView<>();
        loadDB();

        grid.add(cusReceiptList, 0, 0);
        GridPane.setRowSpan(cusReceiptList, grid.getRowCount() + 1);
        GridPane.setColumnSpan(cusReceiptList, grid.getColumnCount() + 1);
        GridPane.setHalignment(cusReceiptList, HPos.LEFT);

        lblDate = new Label("From Date:");
        lblToDate = new Label("to Date: ");
        datepicker1 = new DatePicker();
        datepicker1.setPrefWidth(100);
        //datepicker1.setValue(LocalDate.now());
        datepicker1.setEditable(true);
        datepicker2 = new DatePicker();
        datepicker2.setPrefWidth(100);
       //datepicker2.setValue(LocalDate.now());
        datepicker2.setEditable(true);


        cus_IdCombo = new ComboBox<String>();
        cus_IdCombo.setEditable(true);
        new AutoCompleteComboBoxListener<>(cus_IdCombo);
        cus_IdCombo.setPrefWidth(80);
        cus_IdCombo.setValue("All*");


        HBox hbox = new HBox();
        hbox.getChildren().addAll(lblDate, datepicker1, lblToDate, datepicker2, btnGo);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        grid.add(hbox, 2, 0);


        VBox vBox = new VBox();
        vBox.getChildren().addAll(btnEdit, btnDelete);
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.CENTER);
        grid.add(vBox, 2, 1);


        //grid constraints
        grid.setPrefWidth(700);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new Insets(5));

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPrefWidth(350);
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
                    cusReceiptList.setPrefHeight(scene.getHeight());
                });
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> {
                    grid.setPrefWidth(scene.getWidth());
                });


        //the stage
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Customer Receipt List");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 850) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 700) / 2);
        stage.setWidth(850);
        stage.setHeight(700);
        stage.initModality(Modality.APPLICATION_MODAL);


        stage.show();


        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM customer ORDER BY cus_id");

            //add an empty item just for the "no-choice"
            cus_IdCombo.getItems().add("All*");
            while (resultSet.next()) {

                Customer c = new Customer(
                        resultSet.getString("cus_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email")
                );

                cus_IdCombo.getItems().add(c.getId());
            } //while
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        cus_IdCombo.setOnAction(e->{
            if (cus_IdCombo.getValue().equals("All*")){
               // custOrderList.getItems().clear();
               // loadDB();
            } else {
              //  custOrderList.getItems().clear();
              //  loadDBbyCustomer(cus_IdCombo.getValue());
            }
        });


        btnGo.setOnAction(e -> {
            cusReceiptList.getItems().clear();
            loadDBbyDate(datepicker1.getValue(), datepicker2.getValue());

        });


        //Edit Button
        btnEdit.setOnAction(e -> {
            CustomerReceipt cr = cusReceiptList.getSelectionModel().getSelectedItem();
            int receiptNo = Integer.parseInt(cr.getNumber());
            LocalDate date = cr.getDate();
            String cusId = cr.getCusId();
            String customerName = cr.getCustomerName();
            Double amount = cr.getAmount();
            String notes = cr.getNotes();
            String payment = cr.getSelect();
            EditCustomerReceiptForm crf = new EditCustomerReceiptForm(conn, date, receiptNo, cusId, customerName, amount, payment, notes);

        });

        //Delete Button
        btnDelete.setOnAction(e -> {
            MessageBoxCancelYes m = new MessageBoxCancelYes("Are you sure you want to delete the receipt?", "Warning!");
            boolean response = m.getResponse();
            if (response) {

                //remove data from the list
                CustomerReceipt cr = cusReceiptList.getSelectionModel().getSelectedItem();
                cusReceiptList.getItems().remove(cr);
                if (cusReceiptList.getItems().size() > 0)
                    cusReceiptList.getSelectionModel().select(0);
                else {
                    MessageBoxOK mb = new MessageBoxOK("The list is empty", "INFO");
                }


                //remove receipt from the DB
                try {
                    String delQuery = "DELETE FROM cus_receipts " +
                            " WHERE receipt_no = '" + cr.getNumber()  + "'";

                    PreparedStatement statement = connection.prepareStatement(delQuery);
                    statement.executeUpdate();

                    statement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                MessageBoxOK mb = new MessageBoxOK("The receipt deleted succesfully", "INFO");

            }
        });

    }

    public void loadDB() {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM cus_receipts" +
                    " ORDER BY receipt_date, receipt_no");

            while (resultSet.next()) {
                CustomerReceipt cr = new CustomerReceipt(
                        resultSet.getDate("receipt_date").toLocalDate(),
                        resultSet.getInt("receipt_no"),
                        resultSet.getString("cus_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("select_option"),
                        resultSet.getString("notes")
                );

                cusReceiptList.getItems().add(cr);
            }
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void loadDBbyDate(LocalDate datepicker1, LocalDate datepicker2){
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM cus_receipts WHERE receipt_date BETWEEN '" + datepicker1 + "'" +
                    "AND  '" + datepicker2 + "'" +
                    " ORDER BY receipt_date, receipt_no");

            while (resultSet.next()) {
                CustomerReceipt cr = new CustomerReceipt(
                        resultSet.getDate("receipt_date").toLocalDate(),
                        resultSet.getInt("receipt_no"),
                        resultSet.getString("cus_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("select_option"),
                        resultSet.getString("notes")
                );

                cusReceiptList.getItems().add(cr);
            }
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    public void loadDBbyCustomer(String customerId) {

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM shipping_orders WHERE cus_id = '" + customerId + "'" +
                    " ORDER BY ship_no");

            while (resultSet.next()) {
                CustOrder co = new CustOrder(
                        resultSet.getInt("ship_no"),
                        resultSet.getString("cus_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getDate("ship_date").toLocalDate()
                );

           //     custOrderList.getItems().add(co);
            }
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}