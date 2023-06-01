package Data;

import java.time.LocalDateTime;
import java.util.Date;

public class Transaction {

    public BankAccount from;
    public BankAccount to;
    public float total;
    public String purpose;
    public Date timestamp;

    public Transaction(BankAccount f, BankAccount t, float total, String p, Date time) {
        from = f;
        to = t;
        this.total = total;
        purpose = p;
        timestamp = time;
    }

    public Transaction() {}
}
