import java.io.Serializable;
import java.time.LocalDate;

public class Sales implements Serializable {
    private int no;
    private LocalDate date;
    private String cusId;
    private String customerName;
    private double amount;


    public Sales(){
    }

    public Sales(int no, LocalDate date, String cusId, String customerName, double amount) {
        this.no = no;
        this.date = date;
        this.cusId = cusId;
        this.customerName = customerName;
        this.amount = amount;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Sales{" +
                "no=" + no +
                ", date=" + date +
                ", cusId='" + cusId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", amount=" + amount +
                '}';
    }
}