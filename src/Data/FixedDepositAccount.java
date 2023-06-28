package Data;

import java.util.Date;

/**
 * Fixed deposit accounts only provide access to the capital once the minimum term has been exceeded.
 *
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class FixedDepositAccount extends BankAccount {
    /**
     * Access date til the money can be moved
     */
    public Date accessDate;
}
