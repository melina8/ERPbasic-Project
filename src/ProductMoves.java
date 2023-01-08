import java.io.Serializable;
import java.time.LocalDate;

public class ProductMoves implements Serializable {
    private String type;
    private int no;
    private LocalDate date;
    private String cus_supName;
    private int items;


    public ProductMoves(){
    }

    public ProductMoves(String type, int no, LocalDate date, String cus_supName, int items) {
        this.type = type;
        this.no = no;
        this.date = date;
        this.cus_supName = cus_supName;
        this.items = items;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCus_supName() {
        return cus_supName;
    }

    public void setCus_supName(String cus_supName) {
        this.cus_supName = cus_supName;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "ProductMoves{" +
                "type='" + type + '\'' +
                ", no=" + no +
                ", date=" + date +
                ", cus_supName='" + cus_supName + '\'' +
                ", items=" + items +
                '}';
    }
}
