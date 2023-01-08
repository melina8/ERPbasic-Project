import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.util.converter.DoubleStringConverter;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;


public class EarningsForm {
    Stage stage;
    Label lblDate, lblToDate, lblTotalEarnings;
    TextField txtTotalEarnings;
    DatePicker datepicker1, datepicker2;
    Button btnGo, btnOpen, btnRefresh;
    HBox hboxInfo, hboxInfo2, hboxTable, hboxEarnings;

    TableView<CustomerReceipt> tableView;
    TableColumn<CustomerReceipt, LocalDate> dateColumn;
    TableColumn<CustomerReceipt, String> noColumn;
    TableColumn<CustomerReceipt, String> cusIdColumn;
    TableColumn<CustomerReceipt, String> customerNameColumn;
    TableColumn<CustomerReceipt, Double> amountColumn;
    TableColumn<CustomerReceipt, String> paymentColumn;
    TableColumn<CustomerReceipt, String> notesColumn;

    double total = 0.0;
    double total2 = 0.0;

    Connection connection;

    ObservableList<CustomerReceipt> EarningsList = FXCollections.observableArrayList();

    public EarningsForm(Connection conn) {

        connection = conn;

        //grid
        GridPane grid = new GridPane();
        grid.setHgap(7);
        grid.setVgap(7);


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

        btnGo = new Button("GO");
        btnOpen = new Button("Open Receipt");
        btnRefresh = new Button("Refresh");


        hboxInfo = new HBox();
        hboxInfo.getChildren().addAll(lblDate, datepicker1, lblToDate, datepicker2, btnGo);
        hboxInfo.setSpacing(5);
        hboxInfo.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(hboxInfo, 4);
        grid.add(hboxInfo, 0, 1);


        hboxInfo2 = new HBox();
        hboxInfo2.getChildren().addAll(btnOpen, btnRefresh);
        hboxInfo2.setSpacing(30);
        hboxInfo2.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(hboxInfo2, 3);
        grid.add(hboxInfo2, 3, 1);

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
        dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setMinWidth(130);

        noColumn = new TableColumn<>("No");
        noColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        noColumn.setMinWidth(30);

        cusIdColumn = new TableColumn<>("Customer Id");
        cusIdColumn.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        cusIdColumn.setMinWidth(100);

        customerNameColumn = new TableColumn<>("Customer Name");
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerNameColumn.setMinWidth(250);

        amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setMinWidth(150);

        paymentColumn = new TableColumn<>("Payment type");
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
        paymentColumn.setMinWidth(90);

        notesColumn = new TableColumn<>("Notes");
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesColumn.setMinWidth(250);

        tableView = new TableView<>();
        tableView.getColumns().add(noColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(cusIdColumn);
        tableView.getColumns().add(customerNameColumn);
        tableView.getColumns().add(amountColumn);
        tableView.getColumns().add(paymentColumn);
        tableView.getColumns().add(notesColumn);

        tableView.setEditable(true);

        noColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        cusIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        customerNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        paymentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        notesColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        tableView.setEditable(false);


        tableView.setPrefWidth(1000);
        tableView.setPrefHeight(900);


        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        hboxTable = new HBox();
        hboxTable.getChildren().addAll(tableView);
        hboxTable.setPadding(new Insets(20, 20, 20, 20));
        hboxTable.setAlignment(Pos.CENTER);

        //earnings
        lblTotalEarnings = new Label("Earnings:");
        txtTotalEarnings= new TextField();
        txtTotalEarnings.setPrefWidth(145);

        hboxEarnings = new HBox();
        hboxEarnings.getChildren().addAll(lblTotalEarnings, txtTotalEarnings);
        hboxEarnings.setSpacing(10);
        hboxEarnings.setPadding(new Insets(0, 40, 10, 10));
        hboxEarnings.setAlignment(Pos.CENTER_RIGHT);


        VBox pane = new VBox();
        pane.getChildren().addAll(grid,hboxTable, hboxEarnings);

        btnGo.setOnAction(e -> {
            tableView.getItems().clear();
            loadReceiptsByDate(datepicker1.getValue(), datepicker2.getValue());

        });

        //open the receipt by double clicking
        tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                   openReceipt();
                }
            }
        });

       //open the receipt with open button
        btnOpen.setOnAction(e -> {
            openReceipt();
        });


        btnRefresh.setOnAction(e -> {
            loadReceiptsByDate(datepicker1.getValue(), datepicker2.getValue());
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
        stage.setTitle("Earnings");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 1100) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 700) / 2);
        stage.setWidth(1100);
        stage.setHeight(700);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(false);
        stage.show();

    }

    public void openReceipt(){
        CustomerReceipt cre = tableView.getSelectionModel().getSelectedItem();
        int no1 = Integer.parseInt(cre.getNumber());
        LocalDate date1 = cre.getDate();

        try {
            CallableStatement callstmt = connection.prepareCall("call findReceipt(?,?)");
            callstmt.setInt(1, no1);
            callstmt.setObject(2, date1);


            ResultSet newresult = callstmt.executeQuery();

            while(newresult.next()){
                CustomerReceipt cr= new CustomerReceipt(

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
    }

    public void loadReceiptsByDate(LocalDate datepicker1, LocalDate datepicker2){

        tableView.getItems().clear();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT receipt_date, receipt_no, cus_id, customer_name, amount, select_option, notes" +
            " FROM cus_receipts r " +
            " WHERE  receipt_date BETWEEN '" + datepicker1 + "'" +
                    "AND  '" + datepicker2 + "'" +
            " ORDER BY 2, 1 ");

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

                tableView.getItems().add(cr);
            }
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (int i = 0; i < tableView.getItems().size(); i++) {
            total += amountColumn.getCellData(i);
            DecimalFormat df = new DecimalFormat("#.00");
            total2 = Double.parseDouble(df.format(total));
            System.out.println("earnings " + total2);
        }
        System.out.println("total earnings: " + total2);
        txtTotalEarnings.setText(String.valueOf(total2));
        total = 0.0;
        total2 = 0.0;
    }
}