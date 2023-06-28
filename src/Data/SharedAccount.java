package Data;

/**
 * A shared account is a standard checking account that has a second owner.
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class SharedAccount extends BankAccount {
    /**
     * The second owner has full access to this account
     */
    public Person secondOwner;
}
