//import javax.swing.text.html.ListView;
import javafx.scene.control.ListView;
import java.io.Serializable;

public class Stock implements Serializable {


    private String productName;
    private String productDescription;
    private int items;
    private String supplierId;


    public Stock() { }


    public Stock(String productName, String productDescription, int items, String supplierId) {
       this.productName = productName;
       this.productDescription = productDescription;
       this.items = items;
       this.supplierId = supplierId;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }


    @Override
    public String toString() {
        return productName + " " + items;
    }
}
