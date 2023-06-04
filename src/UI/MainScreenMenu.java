package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        transactionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TransferDialog();
            }
        });
        transferMenu.add(transactionItem);
        JMenuItem moveMoney = new JMenuItem("Auf Konto bewegen");
        moveMoney.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MoveMoneyDialog();
            }
        });
        transferMenu.add(moveMoney);
        JMenuItem requestItem = new JMenuItem("Geld anfordern");
        requestItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RequestMoneyDialog();
            }
        });
        transferMenu.add(requestItem);
        JMenuItem standingOrderItem = new JMenuItem("Dauerauftrag");
        transferMenu.add(standingOrderItem);
        JMenuItem plannedTransactionItem = new JMenuItem("Geplante Buchungen");
        transferMenu.add(plannedTransactionItem);
        return transferMenu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Datei");
        JMenuItem loginItem = new JMenuItem("Anmelden");
        loginItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginDialog();
            }
        });
        loginItem.setName("login");
        fileMenu.add(loginItem);
        JMenuItem logoutItem = new JMenuItem("Abmelden");
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserControl.control.logout();
            }
        });
        logoutItem.setName("logout");
        logoutItem.setVisible(false);
        fileMenu.add(logoutItem);
        JMenuItem newAccountItem = new JMenuItem("Neues Konto");
        newAccountItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NewAccountDialog();
            }
        });
        fileMenu.add(newAccountItem);
        JMenuItem openItem = new JMenuItem("Einstellungen");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(UserControl.control.ui, "Es wurde alles eingestellt.", "Einstellungen", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        fileMenu.add(openItem);
        JMenuItem profileItem = new JMenuItem("Profileinstellungen");
        profileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProfileSettingsDialog();
            }
        });
        fileMenu.add(profileItem);
        JMenuItem saveItem = new JMenuItem("Speichern");
        fileMenu.add(saveItem);
        return fileMenu;
    }

    private JMenu createContactMenu() {
        JMenu contactMenu = new JMenu("Kontakte");

        JMenuItem contactsItem = new JMenuItem("Kontakte anzeigen");
        contactsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ShowContacts();
            }
        });
        JMenuItem pendingRequestsItem = new JMenuItem("Ausstehende Anfragen");
        pendingRequestsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ShowRequests();
            }
        });
        JMenuItem addFriendsItem = new JMenuItem("Freunde einladen");
        addFriendsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InviteContacts();
            }
        });
        contactMenu.add(contactsItem);
        contactMenu.add(pendingRequestsItem);
        contactMenu.add(addFriendsItem);

        return contactMenu;
    }

    private JMenu createAdminMenu() {
        JMenu adminMenu = new JMenu("Admin");

        JMenuItem reportItem = new JMenuItem("Report");
        JMenuItem allowAccountItem = new JMenuItem("Konten freigeben");
        JMenuItem editAccountItem = new JMenuItem("Konten bearbeiten");

        adminMenu.add(reportItem);
        adminMenu.add(allowAccountItem);
        adminMenu.add(editAccountItem);

        adminMenu.setVisible(false);

        return adminMenu;
    }
}
