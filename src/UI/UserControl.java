package UI;

import CustomExceptions.DuplicateKeyException;
import Data.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

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

        this.user = database.checkPerson(forename, lastname, password);
        if (user == null) {
            throw new Exception("Anmeldung fehlgeschlagen.");
        }

        loggedIn = true;
        ui.getJMenuBar().getMenu(0).getMenuComponent(0).setVisible(false);
        ui.getJMenuBar().getMenu(0).getMenuComponent(1).setVisible(true);

        ui.refreshBankAccounts();
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
        // TODO: Kontakt hinzufügen
    }

    public void removeFriend(String forename, String lastname) {
        // TODO: Kontakt entfernen
    }

    public void acceptFriend(String forename, String lastname) {
        // TODO: Anfrage annehmen
    }

    public void rejectFriend(String forename, String lastname) {
        // TODO: Anfrage ablehnen
    }

    public void transferMoney(String account, Person[] receivers, float total, String purpose) {
        // TODO: Geld bewegen
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
        return database.getPendigRequests(user);
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
                c.referenceAccount.name = JOptionPane.showInputDialog(ui, "Bitte geben Sie den Namen des Referenzkontos an.", "Referenzkonto", JOptionPane.INFORMATION_MESSAGE);
                b = c;
                break;
            case 3:
                SavingAccount s = new SavingAccount();
                s.owner = user;
                s.name = text;
                s.setOverdraftFacility(dispo);
                s.reference = null;
                b= s;
                break;
            case 4:
                SharedAccount sh = new SharedAccount();
                sh.name = text;
                sh.owner = user;
                sh.setOverdraftFacility(dispo);
                sh.secondOwner = null;
                b = sh;
                break;
        }
        try {
            database.createNewBankAccount(b);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException();
        }
    }
}
