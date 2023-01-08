import java.io.Serializable;

public class Supplier implements Serializable {

    private String sup_id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;


    public Supplier() { }


    public Supplier(String sup_id, String name, String address, String phoneNumber, String email) {

        this.sup_id = sup_id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;

    }


    public String getSup_id() {
        return sup_id;
    }

    public void setSup_id(String id) {
        this.sup_id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return sup_id;
    }
}
