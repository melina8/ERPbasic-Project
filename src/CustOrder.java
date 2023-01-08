import javafx.scene.control.ListView;
import java.io.Serializable;
import java.time.LocalDate;


public class CustOrder implements Serializable {
    private static int shipNo=0;
    private LocalDate date;
    private String customerId;
    private String customerName;
    private String number;
    private ListView<SupItems> items;


    public CustOrder() { }


    public CustOrder(int shipNo, String customerId, String customerName, LocalDate date) {
        this.shipNo+=1;
        this.number = String.valueOf(shipNo);
        this.customerId = customerId;
        this.customerName = customerName;
        this.date = date;
    }

    public static int getShipNo() {
        return shipNo;
    }

    public static void setShipNo(int shipNo) {
        CustOrder.shipNo = shipNo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDateToString() {
        return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
    }


    @Override
    public String toString() {
        return "No: " + getNumber() + " - Date: " + getDateToString() + " - CustomerID: " + getCustomerId();
    }
}
