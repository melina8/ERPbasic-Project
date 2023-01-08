import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class EditCustomerReceiptForm {

    Stage stage;
    Label lblReceipt_no, lblDate, lblCus_id, lblCusName, lblAmount, lblSelect,  lblNotes;
    TextField txtReceipt_no, txtCusName, txtAmount,  txtNotes;
    DatePicker datepicker;
    ComboBox<String> comboSelect;
    ComboBox<String> comboCusId;
    Button btnUpdate, btnCancel;
    HBox hboxInfo, hboxInfo2;

    String resultCusId;

    Connection connection;

    public EditCustomerReceiptForm(Connection conn, LocalDate date, int receiptNo, String cusId, String customerName, Double amount, String payment, String notes) {
        connection = conn;

        CustomerReceipt cr = new CustomerReceipt(date, receiptNo, cusId, customerName, amount, payment, notes);

        //grid
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        //date
        lblDate = new Label("Date: ");
        datepicker = new DatePicker();
        datepicker.setPrefWidth(100);
        datepicker.setEditable(true);


        //receipt_no
        lblReceipt_no = new Label("No:");
        txtReceipt_no = new TextField();
        txtReceipt_no.setPrefWidth(60);
        txtReceipt_no.setEditable(false);


        lblCus_id = new Label("Customer Id:");
        comboCusId = new ComboBox<String>();
        comboCusId.setEditable(true);
        new AutoCompleteComboBoxListener<>(comboCusId);
        comboCusId.setPrefWidth(80);

        lblCusName = new Label("Customer Name:");
        txtCusName = new TextField();
        txtCusName.setPrefWidth(200);

        lblAmount = new Label("Amount:");
        txtAmount = new TextField();
        txtAmount.setPrefWidth(85);
        txtAmount.setEditable(true);

        lblSelect = new Label("Type of payment:");
        comboSelect = new ComboBox<>();
        comboSelect.getItems().addAll("Cash", "Deposit");
        comboSelect.setPrefWidth(90);

        //options = FXCollections.observableArrayList("Cash", "Deposit");
        //final ComboBox<String> comboSelect = new ComboBox<>(options);

        lblNotes = new Label("Notes:");
        txtNotes = new TextField();
        txtNotes.setPrefWidth(900);
        txtNotes.setEditable(true);

        btnUpdate = new Button("Update");
        btnCancel = new Button("Cancel");


        hboxInfo = new HBox();
        hboxInfo.getChildren().addAll(lblDate, datepicker, lblReceipt_no, txtReceipt_no, lblCus_id, comboCusId, lblCusName, txtCusName, lblAmount, txtAmount, lblSelect, comboSelect);
        hboxInfo.setSpacing(10);
        hboxInfo.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(hboxInfo, 8);
        grid.add(hboxInfo, 0, 0);


        hboxInfo2 = new HBox();
        hboxInfo2.getChildren().addAll(lblNotes, txtNotes, btnUpdate, btnCancel);
        hboxInfo2.setSpacing(20);
        hboxInfo2.setMinHeight(100);
        hboxInfo2.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(hboxInfo2, 7);
        grid.add(hboxInfo2, 0, 1);


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


        // load values of the selected receipt
        loadForm(cr);

        //fill the combobox - customer id and fill the customer name automatically
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM customer ORDER BY cus_id");

            //add an empty item just for the "no-choice"
            comboCusId.getItems().add("");
            while (resultSet.next()) {


                Customer c = new Customer(
                        resultSet.getString("cus_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email")
                );

                comboCusId.getItems().add(c.getId());


                comboCusId.setOnAction(e -> {
                    resultCusId = comboCusId.getValue();
                    System.out.println(resultCusId);
                    try {
                        CallableStatement callstmt = conn.prepareCall("call input2(?)");
                        callstmt.setString(1, resultCusId);

                        ResultSet newresult = callstmt.executeQuery();

                        while(newresult.next()){
                            System.out.println(newresult.getString("customer_name"));
                            txtCusName.setText(newresult.getString("customer_name"));
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


        //add receipt
        btnUpdate.setOnAction(e -> {
            MessageBoxCancelYes m = new MessageBoxCancelYes("Are you sure you want to make changes?","Warning!");
            boolean response = m.getResponse();
            if (response) {

            //check if all fields are filled in
            if(datepicker.getValue() == null){
                MessageBoxOK mb = new MessageBoxOK("Please pick a Date.", "WARNING!");
                System.out.println("Please pick a Date.");
                }
            if (comboCusId.getValue() == null){
                MessageBoxOK mb = new MessageBoxOK("Please pick a Customer.", "WARNING!");
                System.out.println("Please pick a Customer.");
            }
            else if (txtAmount.getText().isEmpty()) {
                MessageBoxOK mb = new MessageBoxOK("Please enter amount.", "WARNING!");
                System.out.println("Please enter amount.");
            }
            else if (comboSelect.getValue() == null) {
                MessageBoxOK mb = new MessageBoxOK("Please select payment method.", "WARNING!");
                System.out.println("Please select payment method.");
            }

            //everything is ok! update receipt
            else {
                if (txtNotes.getText().isEmpty()){
                    System.out.println("No notes...");
                    txtNotes.setText(" ");
                }


                System.out.println("Values!!");
                LocalDate firstParam = datepicker.getValue();
                System.out.println(firstParam);
                int secondParam = Integer.parseInt(txtReceipt_no.getText());
                System.out.println(secondParam);
                String thirdParam = comboCusId.getValue();
                System.out.println(thirdParam);
                String forthParam = txtCusName.getText();
                System.out.println(forthParam);
                Double fifthParam = Double.parseDouble(txtAmount.getText());
                System.out.println(fifthParam);
                String sixParam = comboSelect.getValue();
                System.out.println(sixParam);
                String sevenParam = txtNotes.getText();
                System.out.println(sevenParam);


                CustomerReceipt receiptLine = new CustomerReceipt (firstParam, secondParam, thirdParam, forthParam, fifthParam, sixParam, sevenParam);

                try {
                    String updQuery = "UPDATE cus_receipts" +
                            " SET receipt_date = ?, cus_id = ?, customer_name = ?, amount = ?, select_option = ?, notes = ? " +
                            " WHERE receipt_no = ? ";

                    PreparedStatement updStmt = connection.prepareStatement(updQuery);
                    updStmt.setObject(1, receiptLine.getDate());
                    updStmt.setString(2, receiptLine.getCusId());
                    updStmt.setString(3, receiptLine.getCustomerName());
                    updStmt.setDouble(4, receiptLine.getAmount());
                    updStmt.setString(5, receiptLine.getSelect());
                    updStmt.setString(6, receiptLine.getNotes());
                    updStmt.setInt(7, Integer.parseInt(receiptLine.getNumber()));
                    updStmt.executeUpdate();

                    updStmt.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


                MessageBoxOK mb = new MessageBoxOK("Receipt updated successfully.", "INFORMATION!");
                stage.close();
                 }
            }
            else {  //response is no
                loadForm(cr);
            }
        });


        btnCancel.setOnAction(e -> {
            loadForm(cr);
        });



        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10,10,50,10));
        vbox.getChildren().addAll(grid);

        VBox pane = new VBox();
        pane.getChildren().addAll(vbox);

        //move cursor with enter key
        datepicker.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtReceipt_no.requestFocus();
            }
        });
        txtReceipt_no.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                comboCusId.requestFocus();
            }
        });
        comboCusId.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtCusName.requestFocus();
            }
        });
        txtCusName.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtAmount.requestFocus();
            }
        });
        txtAmount.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                comboSelect.requestFocus();
            }
        });
        comboSelect.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                txtNotes.requestFocus();
            }
        });
        txtNotes.setOnKeyPressed( evt ->{
            System.out.println(KeyCode.ENTER + ", " + evt.getCode());
            if(evt.getCode().equals(KeyCode.ENTER)){
                btnUpdate.requestFocus();
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
        stage.setTitle("Edit Customer Receipt");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 1300) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 200) / 2);
        stage.setWidth(1300);
        stage.setHeight(200);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(false);
        stage.show();
    }

    public void loadForm(CustomerReceipt cr){
        datepicker.setValue(cr.getDate());
        txtReceipt_no.setText(cr.getNumber());
        comboCusId.setValue(cr.getCusId());
        txtCusName.setText(cr.getCustomerName());
        txtAmount.setText(String.valueOf(cr.getAmount()));
        comboSelect.setValue(cr.getSelect());
        txtNotes.setText(cr.getNotes());
    }
}
