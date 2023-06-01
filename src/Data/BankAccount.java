package Data;

public class BankAccount {
    public Person owner;
    public String name;
    protected float overdraftFacility;

    public BankAccount(Person p, String n) {
        owner = p;
        name = n;
    }

    public BankAccount() {}

    public float getOverdraftFacility() {
        return overdraftFacility;
    }

    public void setOverdraftFacility(float overdraftFacility) {
        this.overdraftFacility = overdraftFacility;
    }

    @Override
    public String toString() {
        return name;
    }
}
