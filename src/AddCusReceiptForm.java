import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.sql.*;
import java.time.LocalDate;

public class AddCusReceiptForm {

    Stage stage;
    Label lblReceipt_no, lblDate, lblCus_id, lblCusName, lblAmount, lblSelect, lblTotalAmount, lblNotes;
    TextField txtReceipt_no, txtCusName, txtAmount, txtTotalAmount, txtNotes;
    DatePicker datepicker;
    ComboBox<String> comboSelect;
    ComboBox<String> comboCusId;
    Button btnAddReceipt, btnDeleteLine;
    HBox hboxInfo, hboxInfo2,  hboxAmount;
    TableView<CustomerReceipt> tableView;
    TableColumn<CustomerReceipt, DatePicker> dateColumn;
    TableColumn<CustomerReceipt, String> noColumn;
    TableColumn<CustomerReceipt, String> cusIdColumn;
    TableColumn<CustomerReceipt, String> cusNameColumn;
    TableColumn<CustomerReceipt, Double> amountColumn;
    TableColumn<CustomerReceipt, String> selectColumn;
    TableColumn<CustomerReceipt, String> notesColumn;

    int total = 0;

    String resultCusId;

    Connection connection;

    public AddCusReceiptForm(Connection conn) {
        connection = conn;

        //grid
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        //date
        lblDate = new Label("Date: ");
        datepicker = new DatePicker();
        datepicker.setPrefWidth(100);
        datepicker.setValue(LocalDate.now());
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

        lblSelect = new Label("Type of payment:");

        comboSelect = new ComboBox<>();
        comboSelect.getItems().addAll("Cash", "Deposit");
        comboSelect.setPrefWidth(90);

        //options = FXCollections.observableArrayList("Cash", "Deposit");
        //final ComboBox<String> comboSelect = new ComboBox<>(options);

        lblNotes = new Label("Notes:");
        txtNotes = new TextField();
        txtNotes.setPrefWidth(900);
        txtNotes.setPromptText("add some notes (optional)");

        btnAddReceipt = new Button("Add Receipt");
        btnDeleteLine = new Button("Delete Selected Line");


        hboxInfo = new HBox();
        hboxInfo.getChildren().addAll(lblDate, datepicker, lblReceipt_no, txtReceipt_no, lblCus_id, comboCusId, lblCusName, txtCusName, lblAmount, txtAmount, lblSelect, comboSelect);
        hboxInfo.setSpacing(10);
        hboxInfo.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(hboxInfo, 8);
        grid.add(hboxInfo, 0, 0);


        hboxInfo2 = new HBox();
        hboxInfo2.getChildren().addAll(lblNotes, txtNotes, btnAddReceipt, btnDeleteLine);
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

        //query for receiptNo:
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM cus_receipts ORDER BY receipt_no DESC LIMIT 1");

            //check if table is empty
            if (!resultSet.next()) {
                System.out.println("Table is empty");
                txtReceipt_no.setText("1");
            } else {

                do {
                    CustomerReceipt cre = new CustomerReceipt(
                            resultSet.getDate("receipt_date").toLocalDate(),
                            resultSet.getInt("receipt_no"),
                            resultSet.getString("cus_id"),
                            resultSet.getString("customer_name"),
                            resultSet.getDouble("amount"),
                            resultSet.getString("select_option"),
                            resultSet.getString("notes")
                    );
                    String recno = cre.getNumber();
                    int no = Integer.parseInt(recno);

                    int new_no = no += 1;
                    String new_rec = String.valueOf((Integer) new_no);
                    txtReceipt_no.setText(new_rec);
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
                        //System.out.println(resultSupId);

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

        //tableView
        dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setMinWidth(150);

        noColumn = new TableColumn<>("ReceiptNo");
        noColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        noColumn.setMinWidth(100);

        cusIdColumn = new TableColumn<>("CustomerId");
        cusIdColumn.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        cusIdColumn.setMinWidth(150);

        cusNameColumn = new TableColumn<>("CustomerName");
        cusNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        cusNameColumn.setMinWidth(200);

        amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setMinWidth(150);

        selectColumn = new TableColumn<>("Payment Method");
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
        selectColumn.setMinWidth(150);

        notesColumn = new TableColumn<>("Notes");
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesColumn.setMinWidth(370);

        tableView = new TableView<>();
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(noColumn);
        tableView.getColumns().add(cusIdColumn);
        tableView.getColumns().add(cusNameColumn);
        tableView.getColumns().add(amountColumn);
        tableView.getColumns().add(selectColumn);
        tableView.getColumns().add(notesColumn);


        tableView.setEditable(true);

        dateColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        noColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cusIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cusNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        selectColumn.setCellFactory(ComboBoxTableCell.forTableColumn());


        cusNameColumn.setEditable(false);
        noColumn.setEditable(false);

        tableView.setPrefWidth(700);
        tableView.setPrefHeight(900);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Total amount
        lblTotalAmount =  new Label("Total amount:");
        txtTotalAmount= new TextField();
        txtTotalAmount.setPrefWidth(150);

        hboxAmount = new HBox();
        hboxAmount.getChildren().addAll( lblTotalAmount, txtTotalAmount);
        hboxAmount.setSpacing(10);
        hboxAmount.setPadding(new Insets(5,510,0,0));
        hboxAmount.setAlignment(Pos.CENTER_RIGHT);


       //add receipt
        btnAddReceipt.setOnAction(e -> {

            //check if all fields are filled in
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

            //everything is ok! add productline to the DB and the tableView
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

                //add receipt to the DB (table cus_receipts)
                try {
                    String insQuery = "INSERT INTO cus_receipts" +
                            " (receipt_date, receipt_no, cus_id, customer_name, amount, select_option, notes)" +
                            " VALUES(?,?,?,?,?,?,?)";

                    PreparedStatement insStmt = conn.prepareStatement(insQuery);
                    insStmt.setObject(1, receiptLine.getDate());
                    insStmt.setInt(2, Integer.parseInt(receiptLine.getNumber()));
                    insStmt.setString(3, receiptLine.getCusId());
                    insStmt.setString(4, receiptLine.getCustomerName());
                    insStmt.setDouble(5, receiptLine.getAmount());
                    insStmt.setString(6, receiptLine.getSelect());
                    insStmt.setString(7, receiptLine.getNotes());
                    insStmt.executeUpdate();

                    insStmt.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                //add receipt to the tableView
                tableView.getItems().add(receiptLine);
                for (int i=0; i<tableView.getItems().size(); i++){
                    total   += amountColumn.getCellData(i);
                    System.out.println("amount " + total);
                }
                System.out.println("total items: " + total);
                txtTotalAmount.setText(String.valueOf(total));
                total = 0;


                //prepare labels for next insertion
                datepicker.setValue(LocalDate.now());
                int old_receiptNo= Integer.parseInt(receiptLine.getNumber());
                Integer new_receiptNo = old_receiptNo +=1;
                txtReceipt_no.setText(String.valueOf((Integer)new_receiptNo));
                comboCusId.setValue(null);
                txtCusName.clear();
                txtAmount.clear();
                comboSelect.setValue(null);
                txtNotes.clear();


               //calculateTotals();
            }
        });


        btnDeleteLine.setOnAction(e -> {

                MessageBoxCancelYes m = new MessageBoxCancelYes("Are you sure you want to delete \n the selected receipt?","Warning!");
                System.out.println("Are you sure you want to delete the selected receipt?");
                boolean response = m.getResponse();
                if (response) {

                    //remove receipt from the DB
                    try {
                        String delQuery = "DELETE FROM cus_receipts " +
                                " WHERE receipt_no = '" + noColumn.getCellData(tableView.getSelectionModel().getSelectedItem())  + "'";

                        PreparedStatement statement = connection.prepareStatement(delQuery);
                        statement.executeUpdate();

                        statement.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    //remove receipt from the tableView
                    tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItem());
                    for (int i=0; i<tableView.getItems().size(); i++){
                        total   += amountColumn.getCellData(i);
                        System.out.println("amount " + total);
                    }
                    System.out.println("total items: " + total);
                    txtTotalAmount.setText(String.valueOf(total));
                    total = 0;
                }
                else {
                    System.out.println("Deletion cancelled");
                }


        //    calculateTotals();
        });


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
                btnAddReceipt.requestFocus();
            }
        });

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10,10,50,10));
        vbox.getChildren().addAll(grid, tableView, hboxAmount);

        VBox pane = new VBox();
        pane.getChildren().addAll(vbox);


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
        stage.setTitle("Add Customer Receipts");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 1300) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 800) / 2);
        stage.setWidth(1300);
        stage.setHeight(800);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(false);
        stage.show();
    }
}
