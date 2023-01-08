import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;

import java.io.*;
import java.lang.reflect.Array;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CustomerMovesForm {
    Stage stage;
    Label lblCust_id, lblCustName, lblBalance;
    TextField txtCustName, txtBalance;
    ComboBox<String> comboCustId;
    Button btnOpen, btnRefresh;
    HBox hboxInfo, hboxTable, hboxBalance;
    String resultCustId;

    TableView<CustomerMoves> tableView;
    TableColumn<CustomerMoves, String> typeColumn;
    TableColumn<CustomerMoves, LocalDate> dateColumn;
    TableColumn<CustomerMoves, Integer> noColumn;
    TableColumn<CustomerMoves, String> customerNameColumn;
    TableColumn<CustomerMoves, Double> amountColumn;

    double total = 0.0;
    double total2 = 0.0;


    Connection connection;

    ObservableList<CustomerMoves> movesList = FXCollections.observableArrayList();

    public CustomerMovesForm(Connection conn) {
        connection = conn;


        //grid
        GridPane grid = new GridPane();
        grid.setHgap(7);
        grid.setVgap(7);


        lblCust_id = new Label("Customer Id: ");
        comboCustId = new ComboBox<String>();
        comboCustId.setEditable(true);
        new AutoCompleteComboBoxListener<>(comboCustId);
        comboCustId.setPrefWidth(90);


        lblCustName = new Label("Customer Name: ");
        txtCustName = new TextField();
        txtCustName.setPrefWidth(200);

        btnOpen = new Button("Open Order/Receipt");
        btnRefresh = new Button("Refresh");


        hboxInfo = new HBox();
        hboxInfo.getChildren().addAll(lblCust_id, comboCustId, lblCustName, txtCustName, btnOpen, btnRefresh);
        hboxInfo.setSpacing(30);
        hboxInfo.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(hboxInfo, 7);
        grid.add(hboxInfo, 0, 1);


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


                comboCustId.setOnAction(e ->
                loadCustomersMoves()
                );
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        //tableView
        typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setMinWidth(100);

        noColumn = new TableColumn<>("No");
        noColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        noColumn.setMinWidth(100);

        dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setMinWidth(250);

        customerNameColumn = new TableColumn<>("Customer Name");
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerNameColumn.setMinWidth(250);

        amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setMinWidth(195);

        tableView = new TableView<>();
        tableView.getColumns().add(typeColumn);
        tableView.getColumns().add(noColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(customerNameColumn);
        tableView.getColumns().add(amountColumn);

        tableView.setEditable(true);

        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        noColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        tableView.setEditable(false);


        tableView.setPrefWidth(900);
        tableView.setPrefHeight(900);


        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        hboxTable = new HBox();
        hboxTable.getChildren().addAll(tableView);
        hboxTable.setPadding(new Insets(20, 20, 20, 20));
        hboxTable.setAlignment(Pos.CENTER);

        //balance
        lblBalance = new Label("Balance:");
        txtBalance= new TextField();
        txtBalance.setPrefWidth(145);

        hboxBalance = new HBox();
        hboxBalance.getChildren().addAll(lblBalance, txtBalance);
        hboxBalance.setSpacing(10);
        hboxBalance.setPadding(new Insets(0, 90, 10, 10));
        hboxBalance.setAlignment(Pos.CENTER_RIGHT);


        VBox pane = new VBox();
        pane.getChildren().addAll(grid,hboxTable, hboxBalance);

        //open order/receipt by double clicking
        tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    openItem();
                }
            }
        });

        //open order/receipt with open button
        btnOpen.setOnAction(e -> {
            openItem();
        });

        btnRefresh.setOnAction(e -> {
            loadCustomersMoves();
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
        stage.setTitle("Customer Moves");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 1100) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 700) / 2);
        stage.setWidth(1100);
        stage.setHeight(700);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(false);
        stage.show();

    }

    public void openItem(){
        CustomerMoves cm = tableView.getSelectionModel().getSelectedItem();
        int no1 = cm.getNo();
        LocalDate date1 = cm.getDate();
        String type1 = cm.getType();

        if (type1.equals("RECEIPT")) {

            try {
                CallableStatement callstmt = connection.prepareCall("call findReceipt(?,?)");
                callstmt.setInt(1, no1);
                callstmt.setObject(2, date1);


                ResultSet newresult = callstmt.executeQuery();

                while(newresult.next()){
                    CustomerReceipt cr = new CustomerReceipt(

                            newresult.getDate("receipt_date").toLocalDate(),
                            newresult.getInt("receipt_no"),
                            newresult.getString("cus_id"),
                            newresult.getString("customer_name"),
                            newresult.getDouble("amount"),
                            newresult.getString("select_option"),
                            newresult.getString("notes")
                    );

                    LocalDate date = cr.getDate();
                    int receiptNo = Integer.parseInt(cr.getNumber());
                    String cusId = cr.getCusId();
                    String customerName = cr.getCustomerName();
                    Double amount = cr.getAmount();
                    String notes = cr.getNotes();
                    String payment = cr.getSelect();
                    EditCustomerReceiptForm crf = new EditCustomerReceiptForm(connection, date, receiptNo, cusId, customerName, amount, payment, notes);
                }
                newresult.close();
                callstmt.close();
            } catch (SQLException d) {
                d.printStackTrace();
            }



        } else {

            try {
                CallableStatement callstmt = connection.prepareCall("call findOrder(?,?)");
                callstmt.setInt(1, no1);
                callstmt.setObject(2, date1);


                ResultSet newresult = callstmt.executeQuery();

                while(newresult.next()){
                    CustOrder co = new CustOrder(

                            newresult.getInt("ship_no"),
                            newresult.getString("cus_id"),
                            newresult.getString("customer_name"),
                            newresult.getDate("ship_date").toLocalDate()
                    );


                    int ship_no = Integer.parseInt(co.getNumber());
                    LocalDate date = co.getDate();
                    String cusId = co.getCustomerId();
                    String customerName = co.getCustomerName();
                    EditCusOrderForm cof = new EditCusOrderForm(connection, "Edit", ship_no, date, cusId, customerName);

                }
                newresult.close();
                callstmt.close();
            } catch (SQLException d) {
                d.printStackTrace();
            }
        }
    }

    public void loadCustomersMoves(){
        movesList.clear();
        resultCustId = comboCustId.getValue();
        System.out.println(resultCustId);
        try {
            CallableStatement callstmt = connection.prepareCall("call input2(?)");
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

        //load tableView with Customer Moves
        try {
            CallableStatement callstmt = connection.prepareCall("call loadCustomerMoves(?)");
            callstmt.setString(1, resultCustId);

            ResultSet newresult = callstmt.executeQuery();

            while(newresult.next()){
                movesList.add(new CustomerMoves(
                        newresult.getString("TYPE1"),
                        newresult.getInt("NO1"),
                        newresult.getDate("DATE1").toLocalDate(),
                        newresult.getString("customer_name"),
                        newresult.getDouble("AMOUNT")));
                tableView.setItems(movesList);
            }
            newresult.close();
            callstmt.close();
        } catch (SQLException d) {
            d.printStackTrace();
        }

        for (int i = 0; i < tableView.getItems().size(); i++) {
            total += amountColumn.getCellData(i);
            DecimalFormat df = new DecimalFormat("#.00");
            total2 = Double.parseDouble(df.format(total));
            System.out.println("balance " + total2);
        }
        System.out.println("total balance: " + total2);
        txtBalance.setText(String.valueOf(total2));
        total = 0.0;
        total2 = 0.0;
    };
}