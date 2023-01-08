import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.util.converter.DoubleStringConverter;
import java.sql.*;


public class CustomerBalanceForm {
    Stage stage;


    HBox hboxTable;
    TableView<CustomerBalance> tableView;

    TableColumn<CustomerBalance, String> customerIdColumn;
    TableColumn<CustomerBalance, String> customerNameColumn;
    TableColumn<CustomerBalance, Double> costColumn;
    TableColumn<CustomerBalance, Double> receiptColumn;
    TableColumn<CustomerBalance, Double> balanceColumn;

    Connection connection;

    ObservableList<CustomerBalance> balanceList = FXCollections.observableArrayList();

    public CustomerBalanceForm(Connection conn) {

        connection = conn;


        //tableView
        customerIdColumn = new TableColumn<>("Customer Id");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        customerIdColumn.setMinWidth(100);

        customerNameColumn = new TableColumn<>("Customer Name");
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerNameColumn.setMinWidth(450);

        costColumn = new TableColumn<>("Total Costs");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        costColumn.setMinWidth(249);

        receiptColumn = new TableColumn<>("Total Receipts");
        receiptColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        receiptColumn.setMinWidth(249);

        balanceColumn = new TableColumn<>("Customer Balance");
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceColumn.setMinWidth(249);


        tableView = new TableView<>();
        tableView.getColumns().add(customerIdColumn);
        tableView.getColumns().add(customerNameColumn);
        tableView.getColumns().add(costColumn);
        tableView.getColumns().add(receiptColumn);
        tableView.getColumns().add(balanceColumn);


        customerIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        customerNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        costColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        receiptColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        balanceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));


        tableView.setPrefWidth(1300);
        tableView.setPrefHeight(700);
        tableView.setEditable(false);


        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        hboxTable = new HBox();
        hboxTable.getChildren().addAll(tableView);
        hboxTable.setPadding(new Insets(50, 50, 50, 50));
        hboxTable.setAlignment(Pos.CENTER);


        VBox pane = new VBox();
        pane.getChildren().addAll(hboxTable);

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
        stage.setTitle("Balances");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 700) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 600) / 2);
        stage.setWidth(700);
        stage.setHeight(600);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);
        stage.show();


        loadTableView();
    }



    public void loadTableView() {
        Statement stmt = null;
        Stock st = new Stock();
        try {
            CallableStatement callstmt = connection.prepareCall("call loadBalance()");

            ResultSet newresult = callstmt.executeQuery();

            while(newresult.next()){
                balanceList.add(new CustomerBalance(
                        newresult.getString("cus_id"),
                        newresult.getString("customer_name"),
                        newresult.getDouble("COST"),
                        newresult.getDouble("RECEIPT"),
                        newresult.getDouble("BALANCE")));
                tableView.setItems(balanceList);
            }
            newresult.close();
            callstmt.close();
        } catch (SQLException d) {
            d.printStackTrace();
        }
    }
}