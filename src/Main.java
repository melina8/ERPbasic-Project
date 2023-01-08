import javafx.application.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main extends Application {

    @Override
    public void start(Stage stage) {
        //stylesheet:
        //setUserAgentStylesheet(STYLESHEET_CASPIAN);

        TableColumn<SupItems, String> productNameColumn = new TableColumn<>("Product Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));

       Connection conn = null;

        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/productmanager",
            "javamelina2", "123456");

        }catch (SQLException throwables) {
                throwables.printStackTrace();
        }

        /* Menu: Supplier */
        Menu menuSupplier = new Menu("_Supplier");

        MenuItem menuItemCreateSupplier = new MenuItem("_Create/Edit Supplier");

        Connection finalConn = conn;

        menuItemCreateSupplier.setOnAction(e->{
            SupplierForm sf = new SupplierForm(finalConn);
        });
        menuItemCreateSupplier.setAccelerator(
                new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));


        MenuItem menuItemAddSupOrder = new MenuItem("_Add Supplier Order");
        menuItemAddSupOrder.setOnAction(e->{
            AddSupOrderForm sof = new AddSupOrderForm(finalConn);
        });
        menuItemAddSupOrder.setAccelerator(
                new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemAddSupOrder.setMnemonicParsing(true);


        MenuItem menuItemEditSupOrder = new MenuItem("_View/Edit Supplier Order");
        menuItemEditSupOrder.setOnAction(e->{
            RUDSupOrderForm sof = new RUDSupOrderForm(finalConn);
        });
        menuItemEditSupOrder.setAccelerator(
                new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemEditSupOrder.setMnemonicParsing(true);


        menuSupplier.getItems().addAll(menuItemCreateSupplier,new SeparatorMenuItem(),menuItemAddSupOrder, menuItemEditSupOrder);


        /* Menu: Customer */
        Menu menuCustomer = new Menu("_Customer");

        MenuItem menuItemCreateCustomer = new MenuItem("_Create/Edit Customer");
        menuItemCreateCustomer.setOnAction(e->{
            CustomerForm cf = new CustomerForm(finalConn);
        });
        menuItemCreateCustomer.setAccelerator(
                new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));

        MenuItem menuItemShippingOrder= new MenuItem("_Add Shipping Order");
        menuItemShippingOrder.setOnAction(e->{
           AddCusOrderForm sho = new AddCusOrderForm(finalConn);
        });
        menuItemShippingOrder.setAccelerator(
                new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));

        menuItemShippingOrder.setMnemonicParsing(true);

        MenuItem menuItemEditCusOrder = new MenuItem("_View/Edit Shipping Order");
        menuItemEditCusOrder.setOnAction(e->{
            RUDCusOrderForm cof = new RUDCusOrderForm(finalConn);
        });
        menuItemEditCusOrder.setAccelerator(
                new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemEditCusOrder.setMnemonicParsing(true);

        MenuItem menuItemSales = new MenuItem("_Sales");
        menuItemSales.setOnAction(e->{
            SalesForm sf = new SalesForm(finalConn);
        });
        menuItemSales.setAccelerator(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemSales.setMnemonicParsing(true);

        MenuItem menuItemAddCustomerReceipts = new MenuItem("_Add Customer Receipt");
        menuItemAddCustomerReceipts.setOnAction(e->{
            AddCusReceiptForm crf = new AddCusReceiptForm(finalConn);
        });
        menuItemAddCustomerReceipts.setAccelerator(
                new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemAddCustomerReceipts.setMnemonicParsing(true);

        MenuItem menuItemRUDCustomerReceipts = new MenuItem("_View/Edit Customer Receipt");
        menuItemRUDCustomerReceipts.setOnAction(e->{
            RUDReceipt rudR = new RUDReceipt(finalConn);
        });
        menuItemRUDCustomerReceipts.setAccelerator(
                new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemRUDCustomerReceipts.setMnemonicParsing(true);

        MenuItem menuItemEarnings = new MenuItem("Earnings");
        menuItemEarnings.setOnAction(e->{
            EarningsForm ef = new EarningsForm(finalConn);
        });
        menuItemEarnings.setAccelerator(
                new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemEarnings.setMnemonicParsing(true);

        MenuItem menuItemCustomerBalances = new MenuItem("_Customer Balance");
        menuItemCustomerBalances.setOnAction(e->{
            //......................
            CustomerBalanceForm cbf = new CustomerBalanceForm(finalConn);
        });
        menuItemCustomerBalances.setAccelerator(
                new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemCustomerBalances.setMnemonicParsing(true);

        MenuItem menuItemCustomerMoves = new MenuItem("_Customer Moves");
        menuItemCustomerMoves.setOnAction(e->{
            //......................
            CustomerMovesForm cmf = new CustomerMovesForm(finalConn);
        });
        menuItemCustomerMoves.setAccelerator(
                new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemCustomerMoves.setMnemonicParsing(true);

        menuCustomer.getItems().addAll(menuItemCreateCustomer,new SeparatorMenuItem(), menuItemShippingOrder, menuItemEditCusOrder, menuItemSales, new SeparatorMenuItem(),menuItemAddCustomerReceipts,menuItemRUDCustomerReceipts, menuItemEarnings, new SeparatorMenuItem(), menuItemCustomerBalances, menuItemCustomerMoves);

        /* Menu: Products */
        Menu menuProducts = new Menu("_Products");
        MenuItem menuItemAddProduct = new MenuItem("Add/Edit Product");
        menuItemAddProduct.setOnAction(e->{
            ProductForm pf = new ProductForm(finalConn);
        });
        menuItemAddProduct.setAccelerator(
                new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));

        MenuItem menuItemStock= new MenuItem("Stock");
        menuItemStock.setOnAction(e->{
            StockForm sf = new StockForm(finalConn);
        });
        menuItemStock.setAccelerator(
                new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemStock.setMnemonicParsing(true);

        MenuItem menuItemProductMoves = new MenuItem("Product Moves");
        menuItemProductMoves.setOnAction(e->{
            ProductMovesForm pm = new ProductMovesForm(finalConn);
        });
        menuItemProductMoves.setAccelerator(
                new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN));
        menuItemProductMoves.setMnemonicParsing(true);

        menuProducts.getItems().addAll(menuItemAddProduct,new SeparatorMenuItem(), menuItemStock, new SeparatorMenuItem(), menuItemProductMoves);


        /* Menu: Statistics */
        Menu menuStatistics = new Menu("_Statistics");
        MenuItem menuItemStatsProductSupplier = new MenuItem("No of Products per Supplier");
        menuItemStatsProductSupplier.setOnAction(e->{
            StatsProductsPerSupplier sps = new StatsProductsPerSupplier(finalConn);
        });
        MenuItem menuItemStatsSellsMonth = new MenuItem("Sales per Month");
        menuItemStatsSellsMonth.setOnAction(e->{
            StatsSellsPerMonth spm = new StatsSellsPerMonth(finalConn);
        });
        MenuItem menuItemStatsBestProducts = new MenuItem("Best 5 Selling Products");
        menuItemStatsBestProducts.setOnAction(e->{
            StatsBestSellingProducts spm = new StatsBestSellingProducts(finalConn);
        });
        MenuItem menuItemStatsBestCustomers = new MenuItem("Best 5 Customers");
        menuItemStatsBestCustomers.setOnAction(e->{
            StatsBestCustomers spm = new StatsBestCustomers(finalConn);
        });
        menuStatistics.getItems().addAll(menuItemStatsProductSupplier,new SeparatorMenuItem(),
                 menuItemStatsSellsMonth, new SeparatorMenuItem(), menuItemStatsBestProducts, menuItemStatsBestCustomers);


        /* Menu: Help */
        Menu menuHelp = new Menu("_Help");
        MenuItem menuItemHelpContents = new MenuItem("Contents");
        MenuItem menuItemHelpAbout = new MenuItem("About");
        menuHelp.getItems().addAll(menuItemHelpContents, new SeparatorMenuItem(),
                    menuItemHelpAbout);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuCustomer, menuSupplier, menuProducts, menuStatistics, menuHelp);


        HBox menuPane = new HBox();
        menuPane.getChildren().addAll(menuBar);


        HBox content = new HBox();
        ImageView imgERPbasic = new ImageView(new Image("erp_image.png"));
        imgERPbasic.setFitHeight(content.getWidth());
        imgERPbasic.setPreserveRatio(false);

        //ColorAdjust brightness = new ColorAdjust();
        //brightness.setBrightness(0.6);
        ImageView pic;
        //imgERPbasic.setEffect(brightness);

        content.getChildren().add(imgERPbasic);
        content.setAlignment(Pos.CENTER);

        VBox container = new VBox();
        container.getChildren().addAll(menuPane, content);


        Scene scene = new Scene(container);
        container.setPrefWidth(scene.getWidth());
        menuBar.setPrefWidth(scene.getWidth());
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            menuPane.setPrefWidth(scene.getWidth());
            menuBar.setPrefWidth(scene.getWidth());
            imgERPbasic.setFitWidth(container.getWidth());
            imgERPbasic.setPreserveRatio(true);
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            imgERPbasic.setFitHeight(container.getHeight());
            imgERPbasic.setPreserveRatio(true);
        });

        stage.setScene(scene);
        stage.setTitle("ERPbasic");
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth()-500)/2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight()-600)/2);
        stage.setWidth(700);
        stage.setHeight(490);
        stage.setMaximized(false);

        stage.setOnCloseRequest(e->
        {
            try {
                finalConn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        stage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }
}