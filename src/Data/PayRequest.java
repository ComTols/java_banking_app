package Data;

/**
 * Bill that should be paid
 *
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class PayRequest {
    /**
     * Unique ID from database to find this pay request
     */
    public int id;
    /**
     * Bank account, where the money should move to
     */
    public BankAccount from;
    /**
     * Amount as a float with two digits
     */
    public float total;
    /**
     * Description
     */
    public String purpose;
    /**
     * Receiver of this bill, who should pay
     */
    public Person to;

    /**
     * Displays the sender of this pay request
     * @return Object string
     */
    @Override
    public String toString() {
        return from.owner.toString();
    }
}
