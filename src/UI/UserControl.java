package UI;

import Data.BankAccount;
import Data.Database;
import Data.Person;
import Data.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserControl {

    public static UserControl control = new UserControl();

    private Person user = null;
    private char[] password = null;
    private boolean loggedIn = false;
    public MainScreen ui = null;
    private Database database;

    public void setActiveAccount(BankAccount activeAccount) {
        this.activeAccount = activeAccount;
    }

    private BankAccount activeAccount;

    public UserControl() {
        loadData();
    }

    public void setUser(String forename, String lastname) throws IllegalArgumentException {
        if (forename == null || forename.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (lastname == null || lastname.isEmpty()) {
            throw new IllegalArgumentException();
        }
        // TODO: Datenbank abfragen
        this.user = new Person(forename,lastname);
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

    public void createNewBankAccount(int accountType, int parseInt, String text) {
    }
}
