package Data;

/**
 * Money can only be transferred from a savings account to one specific account. No transfer to other accounts is possible.
 *
 * @author Justus Siegert
 * @version v1.0_stable_alpha
 */
public class SavingAccount extends BankAccount {

    /**
     * Reference bank account where money can be moved to
     */
    public BankAccount reference;

    /**
     * Savings account can´t get under 0
     * @param overdraftFacility New maximum dispo
     */
    @Override
    public void setOverdraftFacility(float overdraftFacility) {
        this.overdraftFacility = 0f;
    }
}
