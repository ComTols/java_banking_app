package Data;

public class SavingAccount extends BankAccount {

    public BankAccount reference;

    @Override
    public void setOverdraftFacility(float overdraftFacility) {
        this.overdraftFacility = 0f;
    }
}
