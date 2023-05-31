package Data;

public class SavingAccount extends BankAccount {

    @Override
    public void setOverdraftFacility(float overdraftFacility) {
        this.overdraftFacility = 0f;
    }
}
