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
import javafx.util.converter.IntegerStringConverter;
import java.sql.*;
import java.time.LocalDate;


public class ProductMovesForm {
    Stage stage;
    Label lblProductName, lblProductDescription, lblBalance;
    TextField txtProductDescription, txtBalance;
    ComboBox<String> comboProductName;
    Button btnOpen, btnRefresh;
    HBox hboxInfo, hboxTable, hboxBalance;
    String resultProductName;

    TableView<ProductMoves> tableView;
    TableColumn<ProductMoves, String> typeColumn;
    TableColumn<ProductMoves, Integer> noColumn;
    TableColumn<ProductMoves, LocalDate> dateColumn;
    TableColumn<ProductMoves, String> cus_supNameColumn;
    TableColumn<ProductMoves, Integer> itemsColumn;

    int total = 0;

    Connection connection;

    ObservableList<ProductMoves> movesList = FXCollections.observableArrayList();

    public ProductMovesForm(Connection conn) {
        connection = conn;


        //grid
        GridPane grid = new GridPane();
        grid.setHgap(7);
        grid.setVgap(7);


        lblProductName = new Label("Product Id: ");
        comboProductName = new ComboBox<String>();
        comboProductName.setEditable(true);
        new AutoCompleteComboBoxListener<>(comboProductName);
        comboProductName.setPrefWidth(90);


        lblProductDescription = new Label("Product Description: ");
        txtProductDescription = new TextField();
        txtProductDescription.setPrefWidth(200);

        btnOpen = new Button("Open Order");
        btnRefresh = new Button("Refresh");


        hboxInfo = new HBox();
        hboxInfo.getChildren().addAll(lblProductName, comboProductName, lblProductDescription, txtProductDescription, btnOpen, btnRefresh);
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


        //fill the combobox - product name and fill the product description automatically
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultProduct = stmt.executeQuery("SELECT p.* FROM products p INNER JOIN (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime");

            //add an empty item just for the "no-choice"
            comboProductName.getItems().add("");
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

                comboProductName.getItems().add(p.getProductName());

                comboProductName.setOnAction(e -> {
                    resultProductName = comboProductName.getValue();
                    System.out.println(resultProductName);
                    try {
                        CallableStatement callstmt = conn.prepareCall("call fillDescription(?)");
                        callstmt.setString(1, resultProductName);

                        ResultSet newresult = callstmt.executeQuery();

                        while(newresult.next()){
                            System.out.println(newresult.getString("productdescription"));
                            txtProductDescription.setText(newresult.getString("productdescription"));
                            tableView.getItems().clear();
                            loadProductsMoves();

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

        cus_supNameColumn = new TableColumn<>("Supplier/Customer Name");
        cus_supNameColumn.setCellValueFactory(new PropertyValueFactory<>("cus_supName"));
        cus_supNameColumn.setMinWidth(250);

        itemsColumn = new TableColumn<>("Items");
        itemsColumn.setCellValueFactory(new PropertyValueFactory<>("items"));
        itemsColumn.setMinWidth(195);

        tableView = new TableView<>();
        tableView.getColumns().add(typeColumn);
        tableView.getColumns().add(noColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(cus_supNameColumn);
        tableView.getColumns().add(itemsColumn);

        tableView.setEditable(true);

        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        noColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        cus_supNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        itemsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        tableView.setEditable(false);


        tableView.setPrefWidth(900);
        tableView.setPrefHeight(900);


        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        hboxTable = new HBox();
        hboxTable.getChildren().addAll(tableView);
        hboxTable.setPadding(new Insets(20, 20, 20, 20));
        hboxTable.setAlignment(Pos.CENTER);

        //balance
        lblBalance = new Label("Items left:");
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
            tableView.getItems().clear();
            loadProductsMoves();
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
        stage.setTitle("Product Moves");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 1100) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 700) / 2);
        stage.setWidth(1100);
        stage.setHeight(700);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(false);
        stage.show();

    }

    public void openItem(){
        ProductMoves pm = tableView.getSelectionModel().getSelectedItem();
        int no1 = pm.getNo();
        LocalDate date1 = pm.getDate();
        String type1 = pm.getType();

        if (type1.equals("SUPPLIER")) {

            try {
                CallableStatement callstmt = connection.prepareCall("call findSupOrder(?,?)");
                callstmt.setInt(1, no1);
                callstmt.setObject(2, date1);


                ResultSet newresult = callstmt.executeQuery();

                while(newresult.next()){
                    SupOrder so = new SupOrder(

                            newresult.getInt("rec_no"),
                            newresult.getString("sup_id"),
                            newresult.getString("supplier_name"),
                            newresult.getDate("rec_date").toLocalDate()
                    );

                    int rec_no = Integer.parseInt(so.getNumber());
                    LocalDate date = so.getDate();
                    String supId = so.getSupplierId();
                    String supplierName = so.getSupplierName();
                    EditSupOrderForm sof = new EditSupOrderForm(connection, "Edit", rec_no, date, supId, supplierName);

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

    public void loadProductsMoves(){

        //load tableView with Product Moves
        try {
            CallableStatement callstmt = connection.prepareCall("call loadProductMoves(?)");
            callstmt.setString(1, resultProductName);

            ResultSet newresult = callstmt.executeQuery();

            while(newresult.next()){
                movesList.add(new ProductMoves(
                        newresult.getString("TYPE1"),
                        newresult.getInt("NO1"),
                        newresult.getDate("DATE1").toLocalDate(),
                        newresult.getString("NAME1"),
                        newresult.getInt("ITEMS")));
                tableView.setItems(movesList);
            }
            newresult.close();
            callstmt.close();
        } catch (SQLException d) {
            d.printStackTrace();
        }

        for (int i = 0; i < tableView.getItems().size(); i++) {
            total += itemsColumn.getCellData(i);

        }
        System.out.println("Items left: " + total);
        txtBalance.setText(String.valueOf(total));
        total = 0;
    };
}
