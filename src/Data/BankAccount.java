package Data;

public class BankAccount {
    public Person owner;
    public String name;
    protected float overdraftFacility;

    public float getOverdraftFacility() {
        return overdraftFacility;
    }

    public void setOverdraftFacility(float overdraftFacility) {
        this.overdraftFacility = overdraftFacility;
    }
}
