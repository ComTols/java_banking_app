package UI;

import CustomExceptions.DuplicateKeyException;
import Data.*;

import javax.swing.*;
import java.util.Date;

/**
 * Contains the logic of the program. Call the static instance {@link #control} to operate on the default database or create more instances to work in parallel.
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class UserControl {

    /**
     * Instantiates the default processing instance for global access. Possibly several instances can be created for parallel work.
     */
    public static UserControl control = new UserControl();

    /**
     * Get the current active user
     * @return current user
     */
    public Person getUser() {
        return user;
    }

    /**
     * Current user
     */
    private Person user = null;
    /**
     * The current users password
     */
    private char[] password = null;
    /**
     * Reference to the main window
     */
    public MainScreen ui = null;
    /**
     * The database object to query the database
     */
    private Database database;

    /**
     * Sets the active bank account to the given bank account
     * @param activeAccount given bank account
     */
    public void setActiveAccount(BankAccount activeAccount) {
        this.activeAccount = activeAccount;
    }

    /**
     * Get the currently selected bank account
     * @return selected bank account
     */
    public BankAccount getActiveAccount() {
        return activeAccount;
    }

    /**
     * The currently selected bank account
     */
    private BankAccount activeAccount;

    /**
     * Creates a new logic processing instance and provides a database query instance.
     */
    public UserControl() {
        database = new Database();
        database.connect();
    }

    /**
     * Checks if the given user data is correct and fills the current user data from database
     * @param forename forename to be verified
     * @param lastname lastname to be verified
     * @param password password to be verified
     * @throws Exception thrown, when login failed
     */
    public void login(String forename, String lastname, char[] password) throws Exception {
        if (forename == null || lastname == null || password == null) throw new IllegalArgumentException();
        if (forename.isEmpty() || lastname.isEmpty() || password.length == 0) throw new IllegalArgumentException();

        this.password = password;
        this.user = database.checkPerson(forename, lastname, password);
        if (user == null) {
            throw new Exception("Anmeldung fehlgeschlagen.");
        }

        ui.getJMenuBar().getMenu(0).getMenuComponent(0).setVisible(false);
        ui.getJMenuBar().getMenu(0).getMenuComponent(1).setVisible(true);

        if (user.isAdmin) {
            enableAdminMode();
        }

        ui.refreshBankAccounts();
    }

    /**
     * Try to log in the user with the new credentials
     */
    public void reloadUser() {
        try {
            login(user.forename, user.lastname, password);
        } catch (Exception ignored) {

        }
    }

    /**
     * Sets all related user and bank accounts data to default
     */
    public void logout() {
        user = null;
        password = null;

        activeAccount = null;
        ui.refreshTransactions();
        ui.refreshBankAccounts();

        ui.getJMenuBar().getMenu(0).getMenuComponent(0).setVisible(true);
        ui.getJMenuBar().getMenu(0).getMenuComponent(1).setVisible(false);

        disableAdminMode();
    }

    /**
     * Adds a new friends relationship between the current user and a given one with pending attribute
     * @param forename given users forename
     * @param lastname given users lastname
     */
    public void addFriend(String forename, String lastname) {
        if(user == null) {
            return;
        }
        database.addPendingFriendship(user, new Person(forename, lastname));
    }

    /**
     * Deletes an existing friends relationship between the current user and a given one
     * @param f given users forename
     * @param l given users lastname
     */
    public void removeFriend(String f, String l) {
        if(user == null) {
            return;
        }
        database.deleteFriendship(user, new Person(f, l));
    }

    /**
     * Updates a friends relationship between the current user and a given one from pending to active
     * @param f given users forename
     * @param l given users lastname
     */
    public void acceptFriend(String f, String l) {
        if(user == null) {
            return;
        }
        database.acceptPendingFriendship(user, new Person(f, l));
    }

    /**
     * Deletes an existing friends relationship between the current user and a given one
     * @param f given users forename
     * @param l given users lastname
     */
    public void rejectFriend(String f, String l) {
        if(user == null) {
            return;
        }
        database.deleteFriendship(user, new Person(f, l));
    }

    /**
     * Sends money from a given bank account to the main account from multiple a given users
     * @param account the sender account
     * @param receivers multiple users to receive the money
     * @param total amount as float with two decimal places
     * @param purpose description
     */
    public void transferMoney(BankAccount account, Person[] receivers, float total, String purpose) {

        float totalSum = total * receivers.length;
        if(database.getBankAccountValue(account) - totalSum < -account.getOverdraftFacility()) {
            JOptionPane.showMessageDialog(ui, "Das Konto wäre nach dieser Transaktion zu weit überzogen!", "Dispo überschritten", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Person r : receivers) {
            if (!transactionAllowed(account, r.mainAccountName)) {
                continue;
            }
            database.moneyTransaction(account, r, total, purpose);
        }
    }

    /**
     * Returns all users, who aren´t friends of the current user
     * @return all users, who aren´t friends of current user
     */
    public Person[] getAvailableFriends() {
        if (user == null) {
            return null;
        }
        return database.getAvailableFriends(user);
    }

    /**
     * Returns all users, who send a friend request to the current user
     * @return all users, who send friend request to current user
     */
    public Person[] getPendingRequests() {
        if (user == null) {
            return null;
        }
        return database.getPendingRequests(user);
    }

    /**
     * Gives all users, who are friends of the current user
     * @return friends of current user
     */
    public Person[] getContacts() {
        if (user == null) {
            return null;
        }
        return database.getContacts(user);
    }

    /**
     * Get all bank accounts associated with the current user
     * @return associated bank accounts
     */
    public BankAccount[] getBankAccounts() {
        if (user == null) {
            return null;
        }
        return database.getBankAccounts(user);
    }

    /**
     * Get all bank accounts associated with the current person
     * @param p given person
     * @return associated bank accounts
     */
    public BankAccount[] getBankAccounts(Person p) {
        return database.getBankAccounts(p);
    }

    /**
     * Get all transactions involving the current selected bank account
     * @return all transactions
     */
    public Transaction[] getTransactions() {
        if (activeAccount == null) {
            return null;
        }
        return database.getTransactions(activeAccount);
    }

    /**
     * Sets the main account of the current user to the given bank account
     * @param b given bank account
     */
    public void updateNewMainAccount(BankAccount b) {
        if (user == null) {
            return;
        }
        if(b instanceof CreditAccount || b instanceof SavingAccount || b instanceof FixedDepositAccount) {
            JOptionPane.showMessageDialog(ui, "Sie können nur Girokonten und Gemeinschaftskonten als Standard wählen!", "Kontotyp nicht unterstützt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        database.updateNewMainAccount(user, b);
    }

    /**
     * Compares a given bank account by the name to the current selected bank account
     * @param b given bank account
     * @return true, if the names are equals
     */
    public boolean isActiveBankAccount(BankAccount b) {
        return b.name != null && b.name.equals(activeAccount.name);
    }

    /**
     * Enables the admin mode
     */
    public void enableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(true);
    }

    /**
     * Disable the admin mode
     */
    private void disableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(false);
        hideAdmin();
    }

    /**
     * Shows the admin dashboard in the main window and hied the maine screen
     */
    public void showAdmin() {
        AdminDashboard admin = new AdminDashboard();
        ui.panelMain.setVisible(false);
        ui.setContentPane(admin.getContentPane());
        ui.getJMenuBar().getMenu(3).getItem(0).setVisible(false);
        ui.getJMenuBar().getMenu(3).getItem(1).setVisible(true);
    }

    /**
     * Hides the admin dashboard in the main window and shows the maine screen
     */
    public void hideAdmin() {
        ui.panelMain.setVisible(true);
        ui.setContentPane(ui.panelMain);
        ui.getJMenuBar().getMenu(3).getItem(0).setVisible(true);
        ui.getJMenuBar().getMenu(3).getItem(1).setVisible(false);
    }

    /**
     * Creates a new bank account associated with the current user
     * @param accountType specified the account type
     *                    0: standard bank account
     *                    1: fixed deposit account
     *                    2: credit account
     *                    3: saving account
     *                    4: shared account
     * @param dispo maximum dispo
     * @param text unique name
     * @throws DuplicateKeyException thrown, if the name was already taken
     */
    public void createNewBankAccount(int accountType, float dispo, String text) throws DuplicateKeyException {
        if (user == null) {
            return;
        }
        BankAccount b = null;
        switch (accountType) {
            case 0 -> {
                b = new BankAccount(user, text);
                b.setOverdraftFacility(dispo);
            }
            case 1 -> {
                FixedDepositAccount f = new FixedDepositAccount();
                f.owner = user;
                // TODO: Zugriffsdatum!
                b = f;
            }
            case 2 -> {
                CreditAccount c = new CreditAccount();
                c.owner = user;
                c.name = text;
                c.setOverdraftFacility(dispo);
                c.referenceAccount = new BankAccount();
                c.referenceAccount.name = JOptionPane.showInputDialog(ui, "Bitte geben Sie den Namen des Referenzkontos an.", "Referenzkonto", JOptionPane.QUESTION_MESSAGE);
                b = c;
            }
            case 3 -> {
                SavingAccount s = new SavingAccount();
                s.owner = user;
                s.name = text;
                s.setOverdraftFacility(dispo);
                JComboBox availableReferences = new JComboBox();
                for (BankAccount r : UserControl.control.getBankAccounts()) {
                    availableReferences.addItem(r);
                }
                JOptionPane.showMessageDialog(ui, availableReferences, "Referenzkonto", JOptionPane.QUESTION_MESSAGE);
                s.reference = (BankAccount) availableReferences.getSelectedItem();
                b = s;
            }
            case 4 -> {
                SharedAccount sh = new SharedAccount();
                sh.name = text;
                sh.owner = user;
                sh.setOverdraftFacility(dispo);
                JComboBox availableOwner = new JComboBox();
                for (Person r : UserControl.control.getContacts()) {
                    availableOwner.addItem(r);
                }
                JOptionPane.showMessageDialog(ui, availableOwner, "Referenzkonto", JOptionPane.QUESTION_MESSAGE);
                sh.secondOwner = (Person) availableOwner.getSelectedItem();
                b = sh;
            }
        }
        try {
            database.createNewBankAccount(b);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException();
        }
    }

    /**
     * Sets an account to inactive
     * @param b given bank account
     */
    public void deleteAccount(BankAccount b) {
        database.deleteAccount(b);
    }

    /**
     * Moves money from a given bank account to another given bank account
     * @param f sender bank account
     * @param t receiver bank account
     * @param total amount as float with two decimals
     * @param p description
     */
    public void moveMoney(BankAccount f, BankAccount t, float total, String p) {
        moveMoney( f,  t,  total,  p, false);
    }

    /**
     * Moves money from a given bank account to another given bank account
     * @param f sender bank account
     * @param t receiver bank account
     * @param total amount as float with two decimals
     * @param p description
     * @param isSystem if true, the validation is skipped
     */
    public void moveMoney(BankAccount f, BankAccount t, float total, String p, boolean isSystem) {
        if (!isSystem && !transactionAllowed(f, t)) {
            return;
        }

        if(database.getBankAccountValue(f) - total < f.getOverdraftFacility()) {
            JOptionPane.showMessageDialog(ui, "Das Konto wäre nach dieser Transaktion zu weit überzogen!", "Dispo überschritten", JOptionPane.ERROR_MESSAGE);
            return;
        }

        database.moveMoney(f,t, total, p);
        ui.refreshTransactions();
    }

    /**
     * Get all pay requests associated with the current user
     * @return associated pay requests
     */
    public PayRequest[] getPayRequests() {
        if (user == null) {
            return null;
        }
        return database.getPayRequests(user);
    }

    /**
     * Settles a pay request and deletes it from the open pay requests list
     * @param p given pay request
     * @param from sender bank account
     */
    public void pay(PayRequest p, BankAccount from) {
        if (user == null) {
            return;
        }
        if (!transactionAllowed(p.from, from)) {
            return;
        }
        database.pay(p, from);
        database.deletePayRequest(p);
        ui.refreshTransactions();
    }

    /**
     * Deletes a pay request from the open pay requests list
     * @param p given pay request
     */
    public void deletePayRequest(PayRequest p) {
        database.deletePayRequest(p);
    }

    /**
     * Creates multiple pay requests and sets the new ids to the given objects
     * @param p pay requests to be inserted
     * @return inserted pay requests with the generated ids
     */
    public PayRequest[] createPayRequests(PayRequest[] p) {
        return database.createPayRequests(p);
    }

    /**
     * Checks if the transaction is allowed by account type rules
     * @param from sender bank account
     * @param to receiver bank account
     * @return true, if the transaction is allowed by account type rules
     */
    private boolean transactionAllowed(BankAccount from, BankAccount to) {
        if (from instanceof FixedDepositAccount) {
            if (((FixedDepositAccount) from).accessDate.after(new Date())) {
                JOptionPane.showMessageDialog(ui, "Sie können erst am "+((FixedDepositAccount) from).accessDate+" Ihr Geld bewegen!", "Zugriffsdatum liegt in der Zukunft", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        if (from instanceof SavingAccount) {
            if (!((SavingAccount) from).reference.name.equals(to.name)) {
                JOptionPane.showMessageDialog(ui, "Sie können von einem Sparkonto nur auf Ihr Referenzkonto "+((SavingAccount) from).reference.name+" überweisen!", "Kein Referenzkonto", JOptionPane.ERROR_MESSAGE);
                return false;

            }
        }
        if (to instanceof CreditAccount) {
            JOptionPane.showMessageDialog(ui, "Sie können auf eine Kreditkarte kein Geld überweisen!", "Ziel ist Kreditkarte", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Checks if the transaction is allowed by account type rules only on sender bank account
     * @param from sender bank account
     * @param to receiver bank account
     * @return true, if the transaction is allowed by account type rules
     */
    private boolean transactionAllowed(BankAccount from, String to) {
        if (from instanceof FixedDepositAccount) {
            if (((FixedDepositAccount) from).accessDate.after(new Date())) {
                JOptionPane.showMessageDialog(ui, "Sie können erst am "+((FixedDepositAccount) from).accessDate+" Ihr Geld bewegen!", "Zugriffsdatum liegt in der Zukunft", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        if (from instanceof SavingAccount) {
            if (!((SavingAccount) from).reference.name.equals(to)) {
                JOptionPane.showMessageDialog(ui, "Sie können von einem Sparkonto nur auf Ihr Referenzkonto "+((SavingAccount) from).reference.name+" überweisen!", "Kein Referenzkonto", JOptionPane.ERROR_MESSAGE);
                return false;

            }
        }
        return true;
    }

    /**
     * Gets all users, who are related to the current admin user
     * @return related users
     */
    public Person[] getRelatedUsers() {
        if(user == null) {
            return null;
        }
        return database.getRelatedUsers(user);
    }

    /**
     * Gets the value of a given bank account
     * @param b given bank account
     * @return total value
     */
    public float getBankAccountValue(BankAccount b) {
        return database.getBankAccountValue(b);
    }

    /**
     * Changes the forename of a given user
     * @param old the old forename
     * @param user user, who will be updated with new forename
     */
    public void changeUserForename(String old, Person user) {
        database.changeUserForename(old, user);
    }

    /**
     * Changes the lastname of a given user
     * @param old the old lastname
     * @param user user, who will be updated with new lastname
     */
    public void changeUserLastname(String old, Person user) {
        database.changeUserLastname(old, user);
    }

    /**
     * Updates the necessary user data of a given user. To update the optional data use {@link #updateUser(Person)}
     * @param user given user
     */
    public void changeUser(Person user) {
        user.isAdmin = user.role.equalsIgnoreCase("bänker");
        database.changeUser(user);
    }

    /**
     * Get all pending bank accounts related to the current admin user including the pending bank accounts from the user himself
     * @return related pending accounts
     */
    public BankAccount[] getPendingAccounts() {
        if(user == null) {
            return null;
        }
        return database.getPendingAccounts(user);
    }

    /**
     * Gets all users who are not managed by an admin
     * @return not managed users
     */
    public Person[] getNotCaredCustomers() {
        return database.getNotCaredCustomers();
    }

    /**
     * Assigns a given user to the current admin
     * @param c given user
     */
    public void careForCustomer(Person c) {
        if(user == null) {
            return;
        }
        database.careForCustomer(user, c);
    }

    /**
     * Creates a given user in the database and generates a password
     * @param u given user
     * @return generated password
     */
    public String createUser(Person u) {
        return database.createUser(u);
    }

    /**
     * Sets a given bank account from pending to active
     * @param b given bank account
     */
    public void acceptBankAccount(BankAccount b) {
        database.acceptBankAccount(b);
    }

    /**
     * Removes a given pending bank account
     * @param b given account
     */
    public void rejectBankAccount(BankAccount b) {
        database.rejectBankAccount(b);
    }

    /**
     * Updates a given user. Note that you can´t update forename or lastname!
     * @param u given user
     */
    public void updateUser(Person u) {
        database.updateUser(u);
        user.date = u.date;
        user.mail = u.mail;
        user.street = u.street;
        user.no = u.no;
        user.postal = u.postal;
        user.phone = u.phone;
    }

    /**
     * Update given standard bank account
     * @param b given bank account with new name
     * @param oldName old name of bank account
     * @throws DuplicateKeyException Thrown, if the new name is already taken
     */
    public void updateAccount(BankAccount b, String oldName) throws DuplicateKeyException {
        database.updateAccount(b, oldName);
    }

    /**
     * Update given standard bank account. Note that you cant update the name. If you want to update the name use {@link #updateAccount(BankAccount, String)}
     * @param b given bank account
     */
    public void updateAccount(BankAccount b) {
        database.updateAccount(b);
    }
}
