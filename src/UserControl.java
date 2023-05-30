import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserControl {

    public static UserControl control = new UserControl();

    private String username = null;
    private char[] password = null;
    private boolean loggedIn = false;
    public MainScreen ui = null;

    public UserControl() {
        loadData();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws IllegalArgumentException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) throws IllegalArgumentException {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException();
        }
        this.password = password;
    }

    public void login() {
        if (username == null || password == null) {
            return;
        }

        // TODO: Check if login credentials are correct
        loggedIn = true;
        ui.getJMenuBar().getMenu(0).getMenuComponent(0).setVisible(false);
        ui.getJMenuBar().getMenu(0).getMenuComponent(1).setVisible(true);
    }

    public void logout() {
        loggedIn = false;
        username = null;
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
