import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Product implements Serializable {
    private String supplierId;
    private String productName;
    private String description;
    private Double unitPrice;
    private Double tax;
    private String taxRate;
    private Timestamp timeStamp;

    public Product() { }

    public Product(String supplierId, String productName, String description, Double unitPrice, String taxRate, Double tax, Timestamp timeStamp) {
        this.supplierId = supplierId;
        this.productName = productName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
        this.tax = tax;
        this.timeStamp = timeStamp;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        Long datetime = System.currentTimeMillis();
        this.timeStamp = new Timestamp(datetime);

    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public String getProductId() {
        return supplierId;
    }

    public void setProductId(String productId) {
        this.supplierId = productId;
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

    @Override
    public String toString() {
        return productName;
    }
}
