import java.io.Serializable;
import java.time.LocalDate;


public class CustomerReceipt implements Serializable {
    private static int receiptNo=0;
    private LocalDate date;
    private String cusId;
    private String customerName;
    private String number;
    private Double amount;
    private String select;
    private String notes;


    public CustomerReceipt() { }


    public CustomerReceipt(LocalDate date, int receiptNo, String cusId, String customerName, double amount, String select, String notes ) {
        this.receiptNo+=1;
        this.date = date;
        this.number = String.valueOf(receiptNo);
        this.cusId = cusId;
        this.customerName = customerName;
        this.amount = amount;
        this.select = select;
        this.notes = notes;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDateToString() {
        return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
    }

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Date: " + getDateToString() + " - No: " + getNumber() +  " - CustomerID: " + getCusId() + " - Amount: " + getAmount();
    }
}
