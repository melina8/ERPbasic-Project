import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import java.sql.*;
import java.time.LocalDate;


public class RUDSupOrderForm {
    Stage stage;

    ListView<SupOrder> supOrderList;
    Label lblChoose;
    ComboBox<String> sup_IdCombo;
    Button btnView, btnEdit, btnDelete;

    ObservableList<SupItems>  forStorage = FXCollections.observableArrayList();

    Connection connection;

    public RUDSupOrderForm(Connection conn) {
        connection = conn;

        // the grid
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
        supOrderList = new ListView<>();
        loadDB();

        grid.add(supOrderList, 0, 0);
        GridPane.setRowSpan(supOrderList, grid.getRowCount() + 1);
        GridPane.setColumnSpan(supOrderList, grid.getColumnCount() + 1);
        GridPane.setHalignment(supOrderList, HPos.LEFT);

        lblChoose = new Label("Select Supplier:");

        sup_IdCombo = new ComboBox<String>();
        sup_IdCombo.setEditable(true);
        new AutoCompleteComboBoxListener<>(sup_IdCombo);
        sup_IdCombo.setPrefWidth(80);
        sup_IdCombo.setValue("All*");


        HBox hbox = new HBox();
        hbox.getChildren().addAll(lblChoose, sup_IdCombo);
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
                    supOrderList.setPrefHeight(scene.getHeight());
                });
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> {
                    grid.setPrefWidth(scene.getWidth());
                });


        //the stage
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Supplier Order List");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 450) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 600) / 2);
        stage.setWidth(500);
        stage.setHeight(600);
        stage.initModality(Modality.APPLICATION_MODAL);


        stage.show();

        //fill the combobox with the ids of the suppliers
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM supplier ORDER BY sup_id");

            //add All* item "
            sup_IdCombo.getItems().add("All*");
            while (resultSet.next()) {

                Supplier s = new Supplier(
                        resultSet.getString("sup_id"),
                        resultSet.getString("company_name"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("email")
                );

                sup_IdCombo.getItems().add(s.getSup_id());
            } //while
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        sup_IdCombo.setOnAction(e -> {
            if (sup_IdCombo.getValue().equals("All*")) {
                supOrderList.getItems().clear();
                loadDB();
            } else {
                supOrderList.getItems().clear();
                loadDBbySupplier(sup_IdCombo.getValue());
            }
        });

        //button actions
        //View Button
        btnView.setOnAction(e -> {
            SupOrder so = supOrderList.getSelectionModel().getSelectedItem();
            int rec_no = Integer.parseInt(so.getNumber());
            LocalDate date = so.getDate();
            String supId = so.getSupplierId();
            String supplierName = so.getSupplierName();
            EditSupOrderForm sof = new EditSupOrderForm(conn, "View", rec_no, date, supId, supplierName);
        });

        //Edit Button
        btnEdit.setOnAction(e -> {
            SupOrder so = supOrderList.getSelectionModel().getSelectedItem();
            int rec_no = Integer.parseInt(so.getNumber());
            LocalDate date = so.getDate();
            String supId = so.getSupplierId();
            String supplierName = so.getSupplierName();
            EditSupOrderForm sof = new EditSupOrderForm(conn, "Edit", rec_no, date, supId, supplierName);
        });

        //Delete Button
        btnDelete.setOnAction(e -> {
            MessageBoxCancelYes m = new MessageBoxCancelYes("Are you sure you want to delete the order?", "Warning!");
            boolean response = m.getResponse();
            if (response) {

                //remove tableview data from storage
                removeFromStorage();

                //remove data from the list
                SupOrder so = supOrderList.getSelectionModel().getSelectedItem();
                supOrderList.getItems().remove(so);
                if (supOrderList.getItems().size() > 0)
                    supOrderList.getSelectionModel().select(0);
                else {
                    MessageBoxOK mb = new MessageBoxOK("The list is empty", "INFO");
                }

                //remove data from received_items
                try {
                    String delQuery2 = "DELETE FROM received_items " +
                            " WHERE rec_no = '" + so.getNumber() + "'";

                    PreparedStatement statement = conn.prepareStatement(delQuery2);
                    statement.executeUpdate();

                    statement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                //remove data from receiving_orders
                try {
                    String delQuery = "DELETE FROM receiving_orders " +
                            " WHERE rec_no = '" + so.getNumber() + "'";

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
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM receiving_orders" +
                    " ORDER BY rec_no");

            while (resultSet.next()) {
                SupOrder so = new SupOrder(
                        resultSet.getInt("rec_no"),
                        resultSet.getString("sup_id"),
                        resultSet.getString("supplier_name"),
                        resultSet.getDate("rec_date").toLocalDate()
                );

                supOrderList.getItems().add(so);
            }
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void loadDBbySupplier(String supplierId) {

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM receiving_orders WHERE sup_id = '" + supplierId + "'" +
                    " ORDER BY rec_no");

            while (resultSet.next()) {
                SupOrder so = new SupOrder(
                        resultSet.getInt("rec_no"),
                        resultSet.getString("sup_id"),
                        resultSet.getString("supplier_name"),
                        resultSet.getDate("rec_date").toLocalDate()
                );

                supOrderList.getItems().add(so);
            }
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


      public void removeFromStorage(){
          forStorage.clear();
          Statement stmt = null;

          SupOrder so = supOrderList.getSelectionModel().getSelectedItem();

          try {
              stmt = connection.createStatement();
              ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT r.productname, p.productDescription, r.pieces " +
                      "FROM received_items r JOIN products p INNER JOIN \n" +
                      "                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime " +
                      "WHERE r.productName = p.productName AND r.rec_no = '" + so.getNumber() + "' " +
                      "ORDER BY productName");

              //check if table is empty
              if (!resultSet.next()) {
                  System.out.println("Table is empty");

              } else {
                  do {
                      SupItems si = new SupItems(

                              resultSet.getString("productName"),
                              resultSet.getString("productDescription"),
                              resultSet.getInt("pieces")
                      );
                      forStorage.add(si);
                  } while (resultSet.next());
              }
              resultSet.close();
          } catch (SQLException throwables) {
              throwables.printStackTrace();
          }
          for (int i=0; i<forStorage.size(); i++){

              try {
                  String updQuery = "UPDATE storage_area" +
                          " SET pieces = pieces - ? " +
                          " WHERE productName = ? ";

                  PreparedStatement updStmt = connection.prepareStatement(updQuery);
                  updStmt.setInt(1, (forStorage.get(i).getItems()));
                  updStmt.setString(2, forStorage.get(i).getProductName());
                  updStmt.executeUpdate();
                  System.out.println("---------------------");

                  updStmt.close();
              } catch (SQLException throwables) {
                  throwables.printStackTrace();
              }
          }
      }
}

