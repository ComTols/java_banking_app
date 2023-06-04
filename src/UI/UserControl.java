package UI;

import CustomExceptions.DuplicateKeyException;
import Data.*;

import javax.swing.*;

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
        database.updateNewMainAccount(user, b);
    }

    public boolean isActiveBankAccount(BankAccount b) {
        return b.name.equals(activeAccount.name);
    }

    private void enableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(true);
    }

    private void disableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(false);
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
        database.moveMoney(f,t, total, p);
        ui.refreshTransactions();
    }
}
