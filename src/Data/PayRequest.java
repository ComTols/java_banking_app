package Data;

public class PayRequest {
    public int id;
    public BankAccount from;
    public float total;
    public String purpose;
    public Person to;

    @Override
    public String toString() {
        return from.owner.toString();
    }
}
