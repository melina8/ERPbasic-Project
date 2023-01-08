import java.io.Serializable;

public class SupItems implements Serializable {
    private String productName;
    private String description;
    private int items;
    private int supOrder_no;

    public SupItems(){

    }

    public SupItems(String productName, String description, int items) {
        this.productName = productName;
        this.description = description;
        this.items = items;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public int getSupOrder_no() {
        return supOrder_no;
    }

    public void setSupOrder_no(int supOrder_no) {
        this.supOrder_no = supOrder_no;
    }
}