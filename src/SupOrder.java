import javafx.scene.control.ListView;
import java.io.Serializable;
import java.time.LocalDate;


public class SupOrder implements Serializable {
    private static int recNo=0;
    private LocalDate date;
    private String supplierId;
    private String supplierName;
    private String number;
    private ListView<SupItems> items;


    public SupOrder() { }


    public SupOrder(int recNo, String supplierId, String supplierName, LocalDate date) {
        this.recNo+=1;
        this.number = String.valueOf(recNo);
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.date = date;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ListView<SupItems> getItems() {
        return items;
    }

    public void setItems(ListView<SupItems> items) {
        this.items = items;
    }

    public int getRecNo() {
        return recNo;
    }

    public LocalDate getDate(){
        return date;
    }

    public String getDateToString() {
        return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }


    @Override
    public String toString() {
        return "No: " + getNumber() + " - Date: " + getDateToString() + " - SupplierID: " + getSupplierId();
    }
}