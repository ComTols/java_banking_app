package UI;

import Data.BankAccount;
import Data.CreditAccount;
import Data.Person;
import Data.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

/**
 * Shows the main window with the transactions of an account.
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class MainScreen extends JFrame {
    /**
     * The panel containing the main content
     */
    public JPanel panelMain;
    /**
     * The bank account selection
     */
    private JComboBox comboBoxAccount;
    /**
     * The table with all transactions
     */
    private JTable table1;
    /**
     * Button to show the accounts details
     */
    private JButton btnAccountInfo;
    /**
     * Label to show the amount of the selected bank account
     */
    private JLabel labelTotal;
    /**
     * Button to delete the bank account
     */
    private JButton btnDelete;
    /**
     * Button to balance the bank account. Only available, if the selected bank account is a credit card
     */
    private JButton btnBalance;
    /**
     * The amount of the bank account
     */
    private float total = 0f;

    /**
     * Shows the main screen
     */
    public MainScreen() {
        setContentPane(panelMain);
        UserControl.control.ui = this;
        createAndShowGui();

        refreshBankAccounts();

        comboBoxAccount.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                UserControl.control.setActiveAccount((BankAccount) comboBoxAccount.getSelectedItem());
                btnBalance.setEnabled(UserControl.control.getActiveAccount() instanceof CreditAccount);
                refreshTransactions();
            }
        });

        btnAccountInfo.addActionListener(e -> showBankAccountInfos());
        btnDelete.addActionListener(e -> deleteAccount());
        btnBalance.addActionListener(e -> balance());
    }

    /**
     * Deletes the selected bank account
     */
    private void deleteAccount() {
        if(total < 0) {
            JOptionPane.showMessageDialog(this, "Sie müssen zuerst das Konto ausgleichen!", "Kontostand negativ", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (UserControl.control.getUser().mainAccountName.equals(((BankAccount)comboBoxAccount.getSelectedItem()).name)) {
            JOptionPane.showMessageDialog(this, "Sie können Ihr Hauptkonto nicht löschen!", "Hauptkonto", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Möchten Sie das Konto wirklich löschen?", "Konto löschen?", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }
        if (total > 0) {
            UserControl.control.transferMoney(
                    (BankAccount)comboBoxAccount.getSelectedItem(),
                    new Person[]{
                            UserControl.control.getUser()
                    },
                    total,
                    "Kontoauflösung"
            );
        }
        UserControl.control.deleteAccount((BankAccount)comboBoxAccount.getSelectedItem());
        refreshBankAccounts();
    }

    /**
     * Shows some information of the selected bank account
     */
    private void showBankAccountInfos() {
        BankAccount b = UserControl.control.getActiveAccount();
        JOptionPane.showMessageDialog(this,
                "<html>Dieses Konto:<br>Name: " + b + "<br>Typ: "+ b.getClass() +"<br>Maximaler Dispokredit: "+b.getOverdraftFacility()+"€</html>", "Kontoinformationen", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Reloads the list of selectable bank accounts
     */
    public void refreshBankAccounts() {
        try {
            boolean first = true;
            ((DefaultComboBoxModel)comboBoxAccount.getModel()).removeAllElements();
            for (BankAccount b : UserControl.control.getBankAccounts()) {
                if (first) {
                    UserControl.control.setActiveAccount(b);
                    first = false;
                }
                comboBoxAccount.addItem(b);
            }
        } catch (NullPointerException ignored) {}
        btnBalance.setEnabled(UserControl.control.getActiveAccount() instanceof CreditAccount);
        refreshTransactions();
    }

    /**
     * Reloads the table to show all transactions
     */
    public void refreshTransactions() {

        float totalAccount = 0.0f;

        int rowCount = table1.getModel().getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            ((DefaultTableModel)table1.getModel()).removeRow(i);
        }
        try {
            for (Transaction t : UserControl.control.getTransactions()) {
                String sender;
                if (UserControl.control.isActiveBankAccount(t.from)) {
                    sender = t.to.owner.toString();
                } else {
                    sender = t.from.owner.toString();
                }
                ((DefaultTableModel)table1.getModel()).addRow(new Object[]{
                        t.timestamp,
                        sender,
                        t.purpose,
                        t.total
                });
                totalAccount += t.total;
            }
        } catch (NullPointerException e) {
            //e.printStackTrace();
        }

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00 €");

        labelTotal.setText("Kontostand: " + decimalFormat.format(totalAccount));
        total = totalAccount;
    }

    /**
     * Set up on program start
     */
    private void createAndShowGui() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(new MainScreenMenu());
        setSize(400, 350);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Bank");
        Program.setIcon(this, "money-coins");
        setLocationByPlatform(true);
        setVisible(true);
        new LoginDialog();
    }

    /**
     * Creates the ui components and is called before the object (InviteContacts) is instantiated
     */
    private void createUIComponents() {
        // Erstellen der Tabelle
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Alle Zellen als nicht editierbar markieren
            }
        };
        model.addColumn("Datum");
        model.addColumn("Gegenseite");
        model.addColumn("Verwendungszweck");
        model.addColumn("Betrag");

        model.setRowCount(100);

        // Erstellen der JTable mit dem TableModel
        table1 = new JTable(model);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowHeight(30);

        // Anpassen der Spaltenbreite
        table1.getColumnModel().getColumn(0).setPreferredWidth(200);
        table1.getColumnModel().getColumn(1).setPreferredWidth(240);
        table1.getColumnModel().getColumn(3).setPreferredWidth(200);

        // Anpassen der Zeilenhöhe
        table1.setRowHeight(30);

        // Automatische Anpassung der Spaltenbreiten
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table1.getColumnModel().getColumn(2).setPreferredWidth(1000);

        // Beträge in rot darstellen
        DefaultTableCellRenderer renderer = new AmountTableCellRenderer(3);

        table1.getColumnModel().getColumn(3).setCellRenderer(renderer);
    }

    /**
     * Balance the selected credit card account
     */
    private void balance() {
        if(!(UserControl.control.getActiveAccount() instanceof CreditAccount)) {
            JOptionPane.showMessageDialog(this, "Nur Kreditkarten können direkt ausgeglichen werden.", "Keine Kreditkarte", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(total >= 0f) {
            return;
        }
        CreditAccount creditAccount = (CreditAccount) UserControl.control.getActiveAccount();

        UserControl.control.moveMoney(creditAccount.referenceAccount, creditAccount,total*-1, "Ausgleichszahlung Kreditkarte", true);
    }

}
