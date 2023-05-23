public class UserControl {

    public static UserControl control = new UserControl();

    private String username = null;
    private char[] password = null;
    private boolean loggedIn = false;
    public MainScreen ui = null;


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

    private void enableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(true);
    }

    private void disableAdminMode() {
        ui.getJMenuBar().getMenu(3).setVisible(false);
    }
}
