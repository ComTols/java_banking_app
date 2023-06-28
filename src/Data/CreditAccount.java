package Data;

/**
 * Credit account cannot receive money directly. The bookings are collected. The account value can be balanced via the reference account.
 * @author Egzon Zenuni
 * @version v1.0_stable_alpha
 */
public class CreditAccount extends BankAccount{
    /**
     * Reference account used to balance
     */
    public BankAccount referenceAccount;
}
