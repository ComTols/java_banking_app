package Data;

import java.util.Date;

/**
 * User with all properties identified by unique key pair of forename and lastname
 *
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class Person {
    /**
     * The users forename
     */
    public String forename;
    /**
     * The users lastname
     */
    public String lastname;
    /**
     * Users Role can be 'Kunde' or 'Bänker'. Other roles are possible but will be handled as 'Kunde'.
     */
    public String role;
    /**
     * Reference to the main account.
     */
    public String mainAccountName;
    /**
     * If the Role is 'Bänker' this will set True
     */
    public boolean isAdmin = false;

    /**
     * The birthday of the user
     */
    public Date date;
    /**
     * The users e-mail
     */
    public String mail;
    /**
     * The users living street
     */
    public String street;
    /**
     * The users house number
     */
    public String no;
    /**
     * The users Postal Code
     */
    public String postal;
    /**
     * The users phone number
     */
    public String phone;

    /**
     * Initialise the user with necessary data
     * @param f Forename of the user
     * @param l LAstname of the user
     * @param r Role of the user
     */
    public Person(String f, String l, String r) {
        forename = f;
        lastname = l;
        role = r;
        if (r.equalsIgnoreCase("Bänker")) {
            isAdmin = true;
        }
    }

    /**
     * Initialise the user with necessary data without role
     * @param f Forename of the user
     * @param l LAstname of the user
     */
    public Person(String f, String l) {
        forename = f;
        lastname = l;
    }

    /**
     * Initialise an empty user object
     */
    public Person() {}

    /**
     * Displays this object as the users name or as 'Extern' if no name is set
     * @return object string
     */
    @Override
    public String toString() {
        if (lastname == null && forename == null) {
            return "Extern";
        }
        return lastname + ", " + forename;
    }
}
