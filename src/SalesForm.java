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
import javafx.util.converter.IntegerStringConverter;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;


public class SalesForm {
    Stage stage;
    Label lblDate, lblToDate, lblTotalSales;
    TextField txtTotalSales;
    DatePicker datepicker1, datepicker2;
    Button btnGo, btnOpen, btnRefresh;
    HBox hboxInfo, hboxInfo2, hboxTable, hboxSales;

    TableView<Sales> tableView;
    TableColumn<Sales, Integer> noColumn;
    TableColumn<Sales, LocalDate> dateColumn;
    TableColumn<Sales, String> cusIdColumn;
    TableColumn<Sales, String> customerNameColumn;
    TableColumn<Sales, Double> amountColumn;

    double total = 0.0;
    double total2 = 0.0;

    Connection connection;

    ObservableList<Sales> salesList = FXCollections.observableArrayList();

    public SalesForm(Connection conn) {
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
        btnOpen = new Button("Open Order");
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
        noColumn = new TableColumn<>("No");
        noColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        noColumn.setMinWidth(100);

        dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setMinWidth(250);

        cusIdColumn = new TableColumn<>("Customer Id");
        cusIdColumn.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        cusIdColumn.setMinWidth(100);

        customerNameColumn = new TableColumn<>("Customer Name");
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerNameColumn.setMinWidth(250);

        amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setMinWidth(195);

        tableView = new TableView<>();
        tableView.getColumns().add(noColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(cusIdColumn);
        tableView.getColumns().add(customerNameColumn);
        tableView.getColumns().add(amountColumn);

        tableView.setEditable(true);

        noColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        cusIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
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

        //sales
        lblTotalSales = new Label("Sales:");
        txtTotalSales= new TextField();
        txtTotalSales.setPrefWidth(145);

        hboxSales = new HBox();
        hboxSales.getChildren().addAll(lblTotalSales, txtTotalSales);
        hboxSales.setSpacing(10);
        hboxSales.setPadding(new Insets(0, 90, 10, 10));
        hboxSales.setAlignment(Pos.CENTER_RIGHT);


        VBox pane = new VBox();
        pane.getChildren().addAll(grid,hboxTable, hboxSales);

        btnGo.setOnAction(e -> {
            tableView.getItems().clear();
            loadSalesByDate(datepicker1.getValue(), datepicker2.getValue());

        });

        //open the order by double clicking
        tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    openOrder();
                }
            }
        });

        //open the order with open button
       btnOpen.setOnAction(e -> {
           openOrder();
       });



        btnRefresh.setOnAction(e -> {
           loadSalesByDate(datepicker1.getValue(), datepicker2.getValue());
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
        stage.setTitle("Sales");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 1100) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 700) / 2);
        stage.setWidth(1100);
        stage.setHeight(700);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(false);
        stage.show();

    }

    public void openOrder(){
        Sales cm = tableView.getSelectionModel().getSelectedItem();
        int no1 = cm.getNo();
        LocalDate date1 = cm.getDate();

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

    public void loadSalesByDate(LocalDate datepicker1, LocalDate datepicker2){

        tableView.getItems().clear();
            Statement stmt = null;
            try {
                stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery("SELECT  s.ship_no, s.ship_date,  c.cus_id, c.customer_name, round(sum(o.finalCost),2) as AMOUNT  \n" +
                        "FROM  customer c JOIN shipping_orders s JOIN ordered_items o \n" +
                        "         WHERE  c.cus_id = s.cus_id AND s.ship_no = o.ship_no AND s.ship_date BETWEEN '" + datepicker1 + "'" +
                        "AND  '" + datepicker2 + "'" +
                        "  GROUP BY s.ship_no ORDER BY 2,1 ");

                while (resultSet.next()) {
                    Sales sa = new Sales(
                            resultSet.getInt("ship_no"),
                            resultSet.getDate("ship_date").toLocalDate(),
                            resultSet.getString("cus_id"),
                            resultSet.getString("customer_name"),
                            resultSet.getDouble("amount")
                    );

                    tableView.getItems().add(sa);
                }
                resultSet.close();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        for (int i = 0; i < tableView.getItems().size(); i++) {
            total += amountColumn.getCellData(i);
            DecimalFormat df = new DecimalFormat("#.00");
            total2 = Double.parseDouble(df.format(total));
            System.out.println("sales " + total2);
        }
        System.out.println("total sales: " + total2);
        txtTotalSales.setText(String.valueOf(total2));
        total = 0.0;
        total2 = 0.0;
    }
}