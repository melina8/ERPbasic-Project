import java.io.Serializable;

public class OrdItems implements Serializable {
    private String productName;
    private String description;
    private int items;
    private int shipOrder_no;
    private double unitCost;
    private double productTax;
    private int discount;
    private double finalCost;

    public OrdItems(){
    }

    public OrdItems(String productName, String description, double unitCost, double productTax, int discount, int items, double finalCost) {

        this.productName = productName;
        this.description = description;
        this.unitCost = unitCost;
        this.productTax = productTax;
        this.discount = discount;
        this.items = items;
        this.finalCost = finalCost;
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

    public int getShipOrder_no() {
        return shipOrder_no;
    }

    public void setShipOrder_no(int shipOrder_no) {
        this.shipOrder_no = shipOrder_no;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getProductTax() {
        return productTax;
    }

    public void setProductTax(double productTax) {
        this.productTax = productTax;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(double finalCost) {
        this.finalCost = finalCost;
    }

    @Override
    public String toString() {
        return "OrdItems{" +
                "productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", items=" + items +
                ", shipOrder_no=" + shipOrder_no +
                ", unitCost=" + unitCost +
                ", productTax=" + productTax +
                ", discount=" + discount +
                ", finalCost=" + finalCost +
                '}';
    }
}