import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import java.sql.*;
import java.time.LocalDate;


public class RUDCusOrderForm {
    Stage stage;


    ListView<CustOrder> custOrderList;
    Button btnView, btnEdit, btnDelete;
    Label lblChoose;
    ComboBox<String> cus_IdCombo;

    ObservableList<OrdItems> forStorage = FXCollections.observableArrayList();

    Connection connection;

    public RUDCusOrderForm(Connection conn) {
        connection = conn;

        //grid
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(5);


        // btnView
        btnView = new Button("VIEW ORDER");

        // btnEdit
        btnEdit = new Button("EDIT ORDER");

        // btnDelete
        btnDelete = new Button("DELETE ORDER");


        // the listview
        custOrderList = new ListView<>();
        loadDB();

        grid.add(custOrderList, 0, 0);
        GridPane.setRowSpan(custOrderList, grid.getRowCount() + 1);
        GridPane.setColumnSpan(custOrderList, grid.getColumnCount() + 1);
        GridPane.setHalignment(custOrderList, HPos.LEFT);

        lblChoose = new Label("Select Customer:");

        cus_IdCombo = new ComboBox<String>();
        cus_IdCombo.setEditable(true);
        new AutoCompleteComboBoxListener<>(cus_IdCombo);
        cus_IdCombo.setPrefWidth(80);
        cus_IdCombo.setValue("All*");


        HBox hbox = new HBox();
        hbox.getChildren().addAll(lblChoose, cus_IdCombo);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        grid.add(hbox, 2, 0);


        VBox vBox = new VBox();
        vBox.getChildren().addAll(btnView, btnEdit, btnDelete);
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.CENTER);
        grid.add(vBox, 2, 1);


        //grid constraints
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
                    custOrderList.setPrefHeight(scene.getHeight());
                });
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> {
                    grid.setPrefWidth(scene.getWidth());
                });


        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Customer Order List");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 450) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 600) / 2);
        stage.setWidth(500);
        stage.setHeight(600);
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
                custOrderList.getItems().clear();
                loadDB();
            } else {
                custOrderList.getItems().clear();
                loadDBbyCustomer(cus_IdCombo.getValue());
            }
            });


        //button actions
        //View Button
        btnView.setOnAction(e -> {
            CustOrder co = custOrderList.getSelectionModel().getSelectedItem();
            int ship_no = Integer.parseInt(co.getNumber());
            LocalDate date = co.getDate();
            String cusId = co.getCustomerId();
            String customerName = co.getCustomerName();
            EditCusOrderForm cof = new EditCusOrderForm(conn, "View", ship_no, date, cusId, customerName);
        });

        //Edit Button
        btnEdit.setOnAction(e -> {
            CustOrder co = custOrderList.getSelectionModel().getSelectedItem();
            int ship_no = Integer.parseInt(co.getNumber());
            LocalDate date = co.getDate();
            String cusId = co.getCustomerId();
            String customerName = co.getCustomerName();
            EditCusOrderForm cof = new EditCusOrderForm(conn, "Edit", ship_no, date, cusId, customerName);
        });

        //Delete Button
        btnDelete.setOnAction(e -> {
            MessageBoxCancelYes m = new MessageBoxCancelYes("Are you sure you want to delete the order?", "Warning!");
            boolean response = m.getResponse();
            if (response) {

                //remove tableview data from storage
                removeFromStorage();

                //remove data from the list
                CustOrder co = custOrderList.getSelectionModel().getSelectedItem();
                custOrderList.getItems().remove(co);
                if (custOrderList.getItems().size() > 0)
                    custOrderList.getSelectionModel().select(0);
                else {
                    MessageBoxOK mb = new MessageBoxOK("The list is empty", "INFO");
                }

                //remove data from ordered_items
                try {
                    String delQuery2 = "DELETE FROM ordered_items " +
                            " WHERE ship_no = '" + co.getNumber() + "'";

                    PreparedStatement statement = conn.prepareStatement(delQuery2);
                    statement.executeUpdate();

                    statement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


                //remove data from shipping_orders
                try {
                    String delQuery = "DELETE FROM shipping_orders " +
                            " WHERE ship_no = '" + co.getNumber() + "'";

                    PreparedStatement statement = conn.prepareStatement(delQuery);
                    statement.executeUpdate();

                    statement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                MessageBoxOK mb = new MessageBoxOK("The order deleted succesfully", "INFO");

            }
        });

    }

    public void loadDB() {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM shipping_orders" +
                    " ORDER BY ship_no");

            while (resultSet.next()) {
                CustOrder co = new CustOrder(
                        resultSet.getInt("ship_no"),
                        resultSet.getString("cus_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getDate("ship_date").toLocalDate()
                );

                custOrderList.getItems().add(co);
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

                custOrderList.getItems().add(co);
            }
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeFromStorage(){
        forStorage.clear();
        Statement stmt = null;

        CustOrder co = custOrderList.getSelectionModel().getSelectedItem();

        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT r.productName, r.unitCost, r.tax, r.discount, r.finalCost, p.productDescription, r.pieces " +
                    "FROM ordered_items r JOIN products p INNER JOIN \n" +
                    "                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime " +
                    "WHERE r.productName = p.productName AND r.ship_no = '" + co.getNumber() + "' " +
                    "ORDER BY productName");

            //check if table is empty
            if (!resultSet.next()) {
                System.out.println("Table is empty");

            } else {
                do {
                    OrdItems or = new OrdItems(

                            resultSet.getString("productName"),
                            resultSet.getString("productDescription"),
                            resultSet.getDouble("unitCost"),
                            resultSet.getDouble("tax"),
                            resultSet.getInt("discount"),
                            resultSet.getInt("pieces"),
                            resultSet.getDouble("finalCost")
                    );

                    forStorage.add(or);
                } while (resultSet.next());
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (int i=0; i<forStorage.size(); i++){

            try {
                String updQuery = "UPDATE storage_area" +
                        " SET pieces = pieces + ? " +
                        " WHERE productName = ? ";

                PreparedStatement updStmt = connection.prepareStatement(updQuery);
                updStmt.setInt(1, (forStorage.get(i).getItems()));
                updStmt.setString(2, forStorage.get(i).getProductName());
                updStmt.executeUpdate();
                System.out.println("---------------------");
                System.out.println("Remove from storage");
                System.out.println("forStorage items: " + forStorage.get(i).getItems());
                System.out.println("forStorage productname: " + forStorage.get(i).getProductName());
                System.out.println("co.getNumber: " + co.getNumber());

                updStmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
