import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;


public class StatsBestSellingProducts {
    Stage stage;
    Label lblDate, lblToDate;
    DatePicker datepicker1, datepicker2;
    Button btnGo;
    HBox hBoxDates;
    PieChart pieChart;

    Connection connection;

    public StatsBestSellingProducts(Connection conn) {
        connection = conn;
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

        hBoxDates = new HBox();
        hBoxDates.getChildren().addAll(lblDate, datepicker1, lblToDate, datepicker2, btnGo);
        hBoxDates.setSpacing(10);
        hBoxDates.setPrefHeight(60);
        hBoxDates.setAlignment(Pos.CENTER);

        pieChart = new PieChart();

        pieChart.setTitle("Best 5 Selling Products");
        pieChart.setPrefWidth(700);
        pieChart.setPrefHeight(700);

        HBox hBox = new HBox(pieChart);
        VBox pane = new VBox();
        pane.getChildren().addAll(hBoxDates, hBox);


        btnGo.setOnAction(e -> {
            pieChart.getData().clear();
            loadDates(datepicker1.getValue(), datepicker2.getValue());
        });


        //scene and stage
        Scene scene = new Scene(pane);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Best 5 Selling Products");

        stage.show();
    }

    public void loadDates(LocalDate datepicker1, LocalDate datepicker2){
        pieChart.getData().clear();

        try {
            Statement stmt = connection.createStatement();

            String query = "select o.productName as product, format(sum(o.finalCost), 2) as sales, sum(o.pieces) as pieces " +
                    "from ordered_items o JOIN shipping_orders s" +
                    " where o.ship_no = s.ship_no AND s.ship_date BETWEEN '" + datepicker1 + "'" +
                    " AND  '" + datepicker2 + "'" +
                    " group by productName " +
                    " order by sum(pieces) desc " +
                    " limit 5";

            ResultSet resultSet = stmt.executeQuery(query);

            while(resultSet.next()) {
                pieChart.getData().add(new PieChart.Data(
                        resultSet.getString("product"),
                        resultSet.getInt("pieces")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}