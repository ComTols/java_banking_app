package UI;

import CustomExceptions.DuplicateKeyException;
import Data.*;

import javax.swing.*;
import java.util.Date;

public class UserControl {

    public static UserControl control = new UserControl();

    public Person getUser() {
        return user;
    }

    private Person user = null;
    private char[] password = null;
    private boolean loggedIn = false;
    public MainScreen ui = null;
    private Database database;

    public void setActiveAccount(BankAccount activeAccount) {
        this.activeAccount = activeAccount;
    }

    public BankAccount getActiveAccount() {
        return activeAccount;
    }

    private BankAccount activeAccount;

    public UserControl() {
        loadData();
    }

    public void login(String forename, String lastname, char[] password) throws Exception {
        if (forename == null || lastname == null || password == null) throw new IllegalArgumentException();
        if (forename.isEmpty() || lastname.isEmpty() || password.length == 0) throw new IllegalArgumentException();

        this.password = password;
        this.user = database.checkPerson(forename, lastname, password);
        if (user == null) {
            throw new Exception("Anmeldung fehlgeschlagen.");
        }

        loggedIn = true;
        ui.getJMenuBar().getMenu(0).getMenuComponent(0).setVisible(false);
        ui.getJMenuBar().getMenu(0).getMenuComponent(1).setVisible(true);

        if (user.isAdmin) {
            enableAdminMode();
        }

        ui.refreshBankAccounts();
    }

    public void reloadUser() {
        try {
            login(user.forename, user.lastname, password);
        } catch (Exception e) {

        }
    }

    public void logout() {
        loggedIn = false;
        user = null;
        password = null;

        activeAccount = null;
        ui.refreshTransactions();
        ui.refreshBankAccounts();

        ui.getJMenuBar().getMenu(0).getMenuComponent(0).setVisible(true);
        ui.getJMenuBar().getMenu(0).getMenuComponent(1).setVisible(false);

        disableAdminMode();
    }

    public void addFriend(String forename, String lastname) {
        if(user == null) {
            new LoginDialog();
            return;
        }
        database.addPendingFriendship(user, new Person(forename, lastname));
    }

    public void removeFriend(String f, String l) {
        if(user == null) {
            new LoginDialog();
            return;
        }
        database.deleteFriendship(user, new Person(f, l));
    }

    public void acceptFriend(String f, String l) {
        if(user == null) {
            new LoginDialog();
            return;
        }
        database.acceptPendingFriendship(user, new Person(f, l));
    }

    public void rejectFriend(String f, String l) {
        if(user == null) {
            new LoginDialog();
            return;
        }
        database.deleteFriendship(user, new Person(f, l));
    }

    public void transferMoney(BankAccount account, Person[] receivers, float total, String purpose) {
        for (Person r : receivers) {
            if (!transactionAllowed(account, r.mainAccountName)) {
                continue;
            }
            database.moneyTransaction(account, r, total, purpose);
        }
    }

    public Person[] getAvailableFriends() {
        if (user == null) {
            new LoginDialog();
            return null;
        }
        return database.getAvailableFriends(user);
    }

    public Person[] getPendigRequests() {
        if (user == null) {
            new LoginDialog();
            return null;
        }
        return database.getPendingRequests(user);
    }

    public Person[] getContacts() {
        if (user == null) {
            new LoginDialog();
            return null;
        }
        return database.getContacts(user);
    }

    public BankAccount[] getBankAccounts() {
        if (user == null) {
            new LoginDialog();
            return null;
        }
        return database.getBankAccounts(user);
    }

    public BankAccount[] getBankAccounts(Person p) {
        return database.getBankAccounts(p);
    }

    public Transaction[] getTransactions() {
        if (activeAccount == null) {
            return null;
        }
        return database.getTransactions(activeAccount);
    }

    public void updateNewMainAccount(BankAccount b) {
        if (user == null) {
            new LoginDialog();
            return;
        }
        if(b instanceof CreditAccount || b instanceof SavingAccount || b instanceof FixedDepositAccount) {
            JOptionPane.showMessageDialog(ui, "Sie können nur Girokonten und Gemeinschaftskonten als Standard wählen!", "Kontotyp nicht unterstützt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        database.updateNewMainAccount(user, b);
    }

    public boolean isActiveBankAccount(BankAccount b) {
        return b.name != null && b.name.equals(activeAccount.name);
    }

    public void enableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(true);
    }

    private void disableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(false);
    }

    public void showAdmin() {
        AdminDashboard admin = new AdminDashboard();
        ui.panelMain.setVisible(false);
        ui.setContentPane(admin.getContentPane());
        ui.getJMenuBar().getMenu(3).getItem(0).setVisible(false);
        ui.getJMenuBar().getMenu(3).getItem(1).setVisible(true);
    }

    public void hideAdmin() {
        ui.panelMain.setVisible(true);
        ui.setContentPane(ui.panelMain);
        ui.getJMenuBar().getMenu(3).getItem(0).setVisible(true);
        ui.getJMenuBar().getMenu(3).getItem(1).setVisible(false);
    }

    private void loadData() {
        database = new Database();
        database.connect();
    }

    public void createNewBankAccount(int accountType, float dispo, String text) throws DuplicateKeyException {
        if (user == null) {
            new LoginDialog();
            return;
        }
        BankAccount b = null;
        switch (accountType) {
            case 0:
                b = new BankAccount(user, text);
                b.setOverdraftFacility(dispo);
                break;
            case 1:
                FixedDepositAccount f = new FixedDepositAccount();
                f.owner = user;
                // TODO: Zugriffsdatum!
                b = f;
                break;
            case 2:
                CreditAccount c = new CreditAccount();
                c.owner = user;
                c.name = text;
                c.setOverdraftFacility(dispo);
                c.referenceAccount = new BankAccount();
                c.referenceAccount.name = JOptionPane.showInputDialog(ui, "Bitte geben Sie den Namen des Referenzkontos an.", "Referenzkonto", JOptionPane.QUESTION_MESSAGE);
                b = c;
                break;
            case 3:
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
                b= s;
                break;
            case 4:
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
                break;
        }
        try {
            database.createNewBankAccount(b);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException();
        }
    }

    public void deleteAccount(BankAccount b) {
        database.deleteAccount(b);
    }

    public void moveMoney(BankAccount f, BankAccount t, float total, String p) {
        if (!transactionAllowed(f, t)) {
            return;
        }
        database.moveMoney(f,t, total, p);
        ui.refreshTransactions();
    }

    public PayRequest[] getPayRequests() {
        if (user == null) {
            new LoginDialog();
            return null;
        }
        return database.getPayRequests(user);
    }

    public void pay(PayRequest p, BankAccount from) {
        if (user == null) {
            new LoginDialog();
            return;
        }
        if (!transactionAllowed(p.from, from)) {
            return;
        }
        database.pay(p, from);
        database.deletePayRequest(p);
        ui.refreshTransactions();
    }

    public void deletePayRequest(PayRequest p) {
        database.deletePayRequest(p);
    }

    public PayRequest[] createPayRequests(PayRequest[] p) {
        return database.createPayRequests(p);
    }

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

    public Person[] getRelatedUsers() {
        if(user == null) {
            new LoginDialog();
            return null;
        }
        return database.getRelatedUsers(user);
    }

    public float getBankAccountValue(BankAccount b) {
        return database.getBankAccountValue(b);
    }

    public void changeUserForename(String old, Person user) {
        database.changeUserForename(old, user);
    }
    public void changeUserLastname(String old, Person user) {
        database.changeUserLastname(old, user);
    }
    public void changeUser(Person user) {
        if(user.role.toLowerCase().equals("bänker")) {
            user.isAdmin = true;
        } else {
            user.isAdmin = false;
        }
        database.changeUser(user);
    }

    public BankAccount[] getPendingAccounts() {
        if(user == null) {
            new LoginDialog();
            return null;
        }
        return database.getPendingAccounts(user);
    }

    public Person[] getNotCaredCustomers() {
        return database.getNotCaredCustomers();
    }

    public void careForCustomer(Person c) {
        if(user == null) {
            new LoginDialog();
            return;
        }
        database.careForCustomer(user, c);
    }
}
