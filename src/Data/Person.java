package Data;

public class Person {
    public String forename;
    public String lastname;
    public String role;
    public String mainAccountName;
    public boolean isAdmin = false;

    public Person(String f, String l, String r) {
        forename = f;
        lastname = l;
        role = r;
    }

    public Person(String f, String l) {
        forename = f;
        lastname = l;
    }

    public Person() {}

    @Override
    public String toString() {
        return lastname + ", " + forename;
    }
}
