import java.io.Serializable;

public class CustomerBalance implements Serializable {
    private String cusId;
    private String customerName;
    private double cost;
    private double amount;
    private double balance;

    public CustomerBalance(){
    }

    public CustomerBalance(String cusId, String customerName, double cost, double amount, double balance) {
        this.cusId = cusId;
        this.customerName = customerName;
        this.cost = cost;
        this.amount = amount;
        this.balance = balance;
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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "CustomerBalance{" +
                "cusId='" + cusId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", cost=" + cost +
                ", amount=" + amount +
                ", balance=" + balance +
                '}';
    }
}
