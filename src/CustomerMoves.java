import java.io.Serializable;
import java.time.LocalDate;

public class CustomerMoves implements Serializable {
    private String type;
    private int no;
    private LocalDate date;
    private String customerName;
    private double amount;


    public CustomerMoves(){
    }

    public CustomerMoves(String type, int no, LocalDate date,  String customerName, double amount) {
        this.type = type;
        this.no = no;
        this.date = date;
        this.customerName = customerName;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
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
        return "CustomerMoves{" +
                "type='" + type + '\'' +
                ", date=" + getDateToString() +
                ", no=" + no +
                ", customerName='" + customerName + '\'' +
                ", amount=" + amount +
                '}';
    }
}
