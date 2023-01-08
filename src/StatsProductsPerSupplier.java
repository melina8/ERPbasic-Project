import javafx.scene.chart.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class StatsProductsPerSupplier {

    Stage stage;

    public StatsProductsPerSupplier(Connection conn) {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Integer> barChart = new BarChart(xAxis,yAxis);


        XYChart.Series<String, Integer> data = new XYChart.Series<>();

       //barChart.lookupAll(".default-color0.chart-bar").forEach(n -> n.setStyle("-fx-bar-fill: blue;"));

        try {
            Statement stmt = conn.createStatement();

            String query = "SELECT sup_id, count(productname) as 'Number of Products' " +
                    "FROM products " +
                    "GROUP BY sup_id " +
                    "ORDER BY sup_id";

            ResultSet resultSet = stmt.executeQuery(query);

            while(resultSet.next()) {
                data.getData().add(new XYChart.Data<>(
                        resultSet.getString("sup_id"),
                        resultSet.getInt("Number of Products")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        data.setName("No of Products per Supplier");

        barChart.getData().add(data);

       //barChart.lookupAll(".default-color0.chart-bar").forEach(n -> n.setStyle("-fx-bar-fill: blue;"));

        HBox hBox = new HBox(barChart);

        //scene and stage
        Scene scene = new Scene(hBox);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("No of Products per Supplier");

        stage.show();
    }
}