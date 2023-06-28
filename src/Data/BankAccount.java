package Data;

/**
 * standard checking account
 *
 * @author Egzon Zenuni
 * @version v1.0_stable_alpha
 */
public class BankAccount {
    /**
     * Owner of this account.
     */
    public Person owner;
    /**
     * Unique account name
     */
    public String name;
    /**
     * Accounts value. In most cases its null, because you should always get it from database.
     */
    public float value;
    /**
     * Maximum dispo
     */
    protected float overdraftFacility;

    /**
     * Create standard bank account and set owner and name on construct
     * @param p The owner of this account
     * @param n Unique name
     */
    public BankAccount(Person p, String n) {
        owner = p;
        name = n;
    }

    /**
     * Create empty account with default values
     */
    public BankAccount() {}

    /**
     * Get maximum dispo from this account. May be overwritten by child classes
     * @return Maximum dispo
     */
    public float getOverdraftFacility() {
        return overdraftFacility;
    }

    /**
     * Set maximum dispo for this account. May be overwritten by child classes
     * @param overdraftFacility New maximum dispo
     */
    public void setOverdraftFacility(float overdraftFacility) {
        this.overdraftFacility = overdraftFacility;
    }

    /**
     * Displays this object by its name
     * @return Unique name
     */
    @Override
    public String toString() {
        return name;
    }
}
