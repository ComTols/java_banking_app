package UI;

import Data.Person;

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

    public void login(String forename, String lastname, char[] password) {
        if (forename == null || lastname == null || password == null) throw new IllegalArgumentException();
        if (forename.isEmpty() || lastname.isEmpty() || password.length == 0) throw new IllegalArgumentException();

        this.password = password;
        this.user = new Person(forename, lastname);

        // TODO: Check if login credentials are correct
        loggedIn = true;
        ui.getJMenuBar().getMenu(0).getMenuComponent(0).setVisible(false);
        ui.getJMenuBar().getMenu(0).getMenuComponent(1).setVisible(true);
    }

    public void logout() {
        loggedIn = false;
        user = null;
        password = null;

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

    public void transferMoney(String account, String receiver, float total, String purpose) {
        // TODO: Geld bewegen
    }

    private void enableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(true);
    }

    private void disableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(false);
    }

    private void loadData() {
        Path p = Paths.get("src/storage/userdata");
        if(!Files.isRegularFile(p)) {
            try {
                Files.createFile(p);
            } catch (IOException e) {
                System.exit(100);
            }
        } else {
            System.out.println("Existiert jawoll!");
        }
    }
}
