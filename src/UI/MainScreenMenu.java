package UI;

import javax.swing.*;

public class MainScreenMenu extends JMenuBar {

    public MainScreenMenu() {
        add(createFileMenu());
        add(createTransferMenu());
        add(createContactMenu());
        add(createAdminMenu());
    }

    private JMenu createTransferMenu() {
        JMenu transferMenu = new JMenu("Transfer");
        JMenuItem transactionItem = new JMenuItem("Überweisen");
        transactionItem.addActionListener(e -> new TransferDialog());
        transferMenu.add(transactionItem);
        JMenuItem moveMoney = new JMenuItem("Auf Konto bewegen");
        moveMoney.addActionListener(e -> new MoveMoneyDialog());
        transferMenu.add(moveMoney);
        JMenuItem requestItem = new JMenuItem("Geld anfordern");
        requestItem.addActionListener(e -> new RequestMoneyDialog());
        transferMenu.add(requestItem);
        JMenuItem payItem = new JMenuItem("Rechnung begleichen");
        payItem.addActionListener(e -> new PayDialog());
        transferMenu.add(payItem);
        return transferMenu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem loginItem = new JMenuItem("Anmelden");
        loginItem.addActionListener(e -> new LoginDialog());
        loginItem.setName("login");
        fileMenu.add(loginItem);
        JMenuItem logoutItem = new JMenuItem("Abmelden");
        logoutItem.addActionListener(e -> UserControl.control.logout());
        logoutItem.setName("logout");
        logoutItem.setVisible(false);
        fileMenu.add(logoutItem);
        JMenuItem newAccountItem = new JMenuItem("Neues Konto");
        newAccountItem.addActionListener(e -> new NewAccountDialog());
        fileMenu.add(newAccountItem);
        JMenuItem openItem = new JMenuItem("Einstellungen");
        openItem.addActionListener(e -> JOptionPane.showMessageDialog(UserControl.control.ui, "Es wurde alles eingestellt.", "Einstellungen", JOptionPane.INFORMATION_MESSAGE));
        fileMenu.add(openItem);
        JMenuItem profileItem = new JMenuItem("Profileinstellungen");
        profileItem.addActionListener(e -> new ProfileSettingsDialog());
        fileMenu.add(profileItem);
        JMenuItem saveItem = new JMenuItem("Speichern");
        saveItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Es wurde alles gespeichert.", "Speichern", JOptionPane.INFORMATION_MESSAGE));
        fileMenu.add(saveItem);
        return fileMenu;
    }

    private JMenu createContactMenu() {
        JMenu contactMenu = new JMenu("Kontakte");

        JMenuItem contactsItem = new JMenuItem("Kontakte anzeigen");
        contactsItem.addActionListener(e -> new ShowContacts());
        JMenuItem pendingRequestsItem = new JMenuItem("Ausstehende Anfragen");
        pendingRequestsItem.addActionListener(e -> new ShowRequests());
        JMenuItem addFriendsItem = new JMenuItem("Freunde einladen");
        addFriendsItem.addActionListener(e -> new InviteContacts());
        contactMenu.add(contactsItem);
        contactMenu.add(pendingRequestsItem);
        contactMenu.add(addFriendsItem);

        return contactMenu;
    }

    private JMenu createAdminMenu() {
        JMenu adminMenu = new JMenu("Admin");

        JMenuItem reportItem = new JMenuItem("Dashboard");
        reportItem.addActionListener(e -> UserControl.control.showAdmin());
        JMenuItem backItem = new JMenuItem("Zurück");
        backItem.addActionListener(e -> UserControl.control.hideAdmin());
        backItem.setVisible(false);
        JMenuItem allowAccountItem = new JMenuItem("Konten freigeben");
        allowAccountItem.addActionListener(e -> new ReleaseAccountDialog());
        JMenuItem unassignedCustomersItem = new JMenuItem("Nicht zugewiesene Kunden");
        unassignedCustomersItem.addActionListener(e -> new NotCaredCustomersDialog());

        JMenuItem createNewUser = new JMenuItem("Neuer Benutzer");
        createNewUser.addActionListener(e -> new CreateUserDialog());

        adminMenu.add(reportItem);
        adminMenu.add(backItem);
        adminMenu.add(allowAccountItem);
        adminMenu.add(unassignedCustomersItem);
        adminMenu.add(createNewUser);

        adminMenu.setVisible(false);

        return adminMenu;
    }
}
