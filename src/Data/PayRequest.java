package Data;

public class PayRequest {
    public int id;
    public Person from;
    public float total;
    public String purpose;

    @Override
    public String toString() {
        return from.toString();
    }
}
