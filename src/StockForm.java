import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.util.converter.IntegerStringConverter;
import java.sql.*;


public class StockForm {
    Stage stage;
    Label lblTotalItems;
    TextField txtTotalItems;


    HBox hboxTable, hboxItems;
    TableView<Stock> tableView;
    int total = 0;

    TableColumn<Stock, String> productNameColumn;
    TableColumn<Stock, String> descriptionColumn;
    TableColumn<Stock, Integer> itemsColumn;
    TableColumn<Stock, String> supplierIdColumn;

    Connection connection;


    ObservableList<Stock> stockList = FXCollections.observableArrayList();

    public StockForm(Connection conn) {

        connection = conn;


        //tableView
        productNameColumn = new TableColumn<>("Product Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameColumn.setMinWidth(300);

        descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("productDescription"));
        descriptionColumn.setMinWidth(650);

        itemsColumn = new TableColumn<>("Items");
        itemsColumn.setCellValueFactory(new PropertyValueFactory<>("items"));
        itemsColumn.setMinWidth(150);

        supplierIdColumn = new TableColumn<>("Supplier Id");
        supplierIdColumn.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        supplierIdColumn.setMinWidth(190);


        tableView = new TableView<>();
        tableView.getColumns().add(productNameColumn);
        tableView.getColumns().add(descriptionColumn);
        tableView.getColumns().add(itemsColumn);
        tableView.getColumns().add(supplierIdColumn);


        productNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        itemsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        supplierIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        tableView.setPrefWidth(1300);
        tableView.setPrefHeight(700);
        tableView.setEditable(true);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        hboxTable = new HBox();
        hboxTable.getChildren().addAll(tableView);
        hboxTable.setPadding(new Insets(50, 50, 50, 50));
        hboxTable.setAlignment(Pos.CENTER);

        //Total Items
        lblTotalItems = new Label("Total Items:");
        txtTotalItems = new TextField();
        txtTotalItems.setPrefWidth(145);

        hboxItems = new HBox();
        hboxItems.getChildren().addAll(lblTotalItems, txtTotalItems);
        hboxItems.setSpacing(10);
        hboxItems.setPadding(new Insets(0, 120, 20, 0));
        hboxItems.setAlignment(Pos.CENTER_RIGHT);


        VBox pane = new VBox();
        pane.getChildren().addAll(hboxTable, hboxItems);

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
        stage.setTitle("Stock");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 700) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 600) / 2);
        stage.setWidth(700);
        stage.setHeight(600);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);
        stage.show();

        loadTableView();


       for (int i = 0; i < tableView.getItems().size(); i++) {
            total += itemsColumn.getCellData(i);
            System.out.println("items " + total);
        }
        System.out.println("total items: " + total);
        txtTotalItems.setText(String.valueOf(total));
        total = 0;


    }

    public void loadTableView() {
        Statement stmt = null;
        Stock st = new Stock();
        try {
            CallableStatement callstmt = connection.prepareCall("call loadStock()");

            ResultSet newresult = callstmt.executeQuery();

            while(newresult.next()){
                stockList.add(new Stock(
                        newresult.getString("productName"),
                        newresult.getString("productDescription"),
                        newresult.getInt("pieces"),
                        newresult.getString("sup_id")));
                tableView.setItems(stockList);
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
}