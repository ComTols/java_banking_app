package Data;

import java.util.Date;

public class Person {
    public String forename;
    public String lastname;
    public String role;
    public String mainAccountName;
    public boolean isAdmin = false;

    public Date date;
    public String mail;
    public String street;
    public String no;
    public String postal;
    public String phone;

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
        if (lastname == null && forename == null) {
            return "Extern";
        }
        return lastname + ", " + forename;
    }
}
