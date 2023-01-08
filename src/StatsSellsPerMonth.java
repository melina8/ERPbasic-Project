import javafx.scene.chart.*;
import javafx.stage.*;
import javafx.scene.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class StatsSellsPerMonth {

    Stage stage;
    BarChart<String, Number> barChart;
    XYChart.Series<String, Number> data;
    Integer z;
    int j;

    public StatsSellsPerMonth(Connection conn) {

        try {
            Statement stmt = conn.createStatement();

            String query = "select  round(sum(o.finalCost),2), MONTHNAME(s.ship_date), YEAR(s.ship_date) \n" +
                    "                    from shipping_orders s JOIN ordered_items o ON s.ship_no = o.ship_no\n" +
                    "                    group by YEAR(s.ship_date), MONTH(s.ship_date)\n" +
                    "                    ORDER BY s.ship_date, s.ship_no";

            ResultSet resultSet = stmt.executeQuery(query);

            ArrayList<Double> sales = new ArrayList<Double>();
            ArrayList<String> months = new ArrayList<String>();
            ArrayList<String> years = new ArrayList<String>();


            while (resultSet.next()) {
                sales.add(resultSet.getDouble(1));
                months.add(resultSet.getString(2));
                years.add(resultSet.getString(3));

            }
            resultSet.close();

                System.out.println(years);
                System.out.println(months);
                System.out.println(sales);

                //BAR CHART
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("Months per Year");
                //xAxis.setTickLabelRotation(45);
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Sales");
                barChart = new BarChart<String, Number>(xAxis, yAxis);


                //for each record in years list
                for (int i = 0; i < years.size(); i++) {

                    //print the position index
                    System.out.println("i: " + i);

                    //check if the value in year list changes (change of the year) or if the loop is finished.
                    // If yes, make a series and add months of the previous year to the series
                    //finally, add all series to the barChart
                    if ( i!=0 && !((years.get(i).equals(years.get(i-1)))) || i==(years.size()-1)) {

                        System.out.println("year.get(i): " + years.get(i));
                        System.out.println("year.get(i-1): " + years.get(i-1));
                        data = new XYChart.Series<>();
                        data.setName(i==(years.size()-1)? years.get(i):years.get(i-1));
                        System.out.println("name: " + (i==(years.size()-1)? years.get(i):years.get(i-1)));
                        System.out.println("data: "+ data);


                        if (z == null){
                            System.out.println("z: " + z);
                            z = 0;
                            System.out.println("z: " + z);
                        }
                        if (i==(years.size()-1)){
                            i +=1;
                        }

                        for (j=z; j < i; j++) {
                            data.getData().add(new XYChart.Data<String, Number>(months.get(j), sales.get(j)));
                            System.out.println("j: " + j + " month: " + months.get(j) + " sales: " + sales.get(j));

                            z = i;
                            System.out.println("z: " + z);
                        }

                        barChart.getData().addAll(data);
                    } //if

                } //for

         } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        //scene and stage
        Scene scene = new Scene(barChart);

        stage = new Stage();
        stage.setScene(scene);
        stage.setHeight(600);
        stage.setWidth(1000);
        stage.setTitle("Sales per Month");

        stage.show();
    }
}