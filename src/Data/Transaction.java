package Data;

import java.util.Date;

/**
 * A transaction is a movement of money.
 * @author Justus Siegert
 * @version v1.0_stable_alpha
 */
public class Transaction {

    /**
     * Money sender account
     */
    public BankAccount from;
    /**
     * Money receiver account
     */
    public BankAccount to;
    /**
     * Amount as float with two digits
     */
    public float total;
    /**
     * Description
     */
    public String purpose;
    /**
     * Date and time when the transaction was registered
     */
    public Date timestamp;

    /**
     * Instantiate a transaction with necessary data
     * @param f Sender account
     * @param t Receiver account
     * @param total Amount
     * @param p Description
     * @param time registered time
     */
    public Transaction(BankAccount f, BankAccount t, float total, String p, Date time) {
        from = f;
        to = t;
        this.total = total;
        purpose = p;
        timestamp = time;
    }

    /**
     * Instantiate an empty transaction
     */
    public Transaction() {}
}
