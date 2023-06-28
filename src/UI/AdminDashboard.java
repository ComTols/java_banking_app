package UI;

import CustomExceptions.DuplicateKeyException;
import Data.BankAccount;
import Data.Person;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Shows the administrator dashboard with an overview of the overdrawn accounts, the associated customers and the accounts of the customers.
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class AdminDashboard extends JFrame {
    /**
     * panel containing the admin content
     */
    private JPanel panelMain;
    /**
     * table containing the Users
     */
    private JTable tableUsers;
    /**
     * table containing the accounts
     */
    private JTable tableAccounts;
    /**
     * table containing the accounts that are in dispo
     */
    private JTable tableAttention;
    private Person[] users;
    private BankAccount[] accounts;
    private BankAccount[] attentions;

    public AdminDashboard() {
        setContentPane(panelMain);
    }

    /**
     * creates the UI-Components and is called before the object (Admin Dashboard) is instantiated
     */
    private void createUIComponents() {
        // Erstellen der Tabelle
        DefaultTableModel modelTableUsers = new DefaultTableModel() {
            /**
             * sets a value in the cell at the given row and column and updates the bank-account
             * @param aValue the value the cell should contain
             * @param row the row of the cell that should be changes
             * @param column the column of the cell that should be changed
             */
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                String old = getValueAt(row, column).toString();
                super.setValueAt(aValue, row, column);
                switch (column) {
                    case 0:
                        users[row].forename = aValue.toString();
                        UserControl.control.changeUserForename(old, users[row]);
                        break;
                    case 1:
                        users[row].lastname = aValue.toString();
                        UserControl.control.changeUserLastname(old, users[row]);
                        break;
                    case 2:
                        users[row].role = aValue.toString();
                        UserControl.control.changeUser(users[row]);
                        break;
                }
            }
        };
        modelTableUsers.addColumn("Vorname");
        modelTableUsers.addColumn("Nachname");
        modelTableUsers.addColumn("Rolle");

        DefaultTableModel modelTableAttention = new DefaultTableModel() {
            /**
             * checks if a cell is editable
             * @param row the row of the cell in question
             * @param column the column of the cell in question
             * @return true if the cell is editable
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 3;
            }
        };
        modelTableAttention.addColumn("Vorname");
        modelTableAttention.addColumn("Nachname");
        modelTableAttention.addColumn("Konto");
        modelTableAttention.addColumn("Aktueller Kontostand");
        modelTableAttention.addColumn("Maximaler Dispo");

        DefaultTableModel modelTableAccounts = new DefaultTableModel() {
            /**
             * checks if the cell is editable
             * @param row the row of the cell in question
             * @param column the column of the cell in question
             * @return true if the cell is editable
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 2 && column != 1;
            }

            /**
             * sets a value in the cell at the given row and column and updates the bank-account
             * @param aValue the value
             * @param row the row of the cell
             * @param column the column of the cell
             */
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                Object old = getValueAt(row, column);
                super.setValueAt(aValue, row, column);
                switch (column) {
                    case 0:
                        accounts[row].name = aValue.toString();
                        try {
                            UserControl.control.updateAccount(accounts[row], old.toString());
                        } catch (DuplicateKeyException e) {
                            JOptionPane.showMessageDialog(UserControl.control.ui, "Der Kontoname konnte nicht geändert werden. Er ist bereits in Nutzung.", "Name doppelt", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case 3:
                        float dispo = -1f;
                        try {
                            dispo = Math.round(Float.parseFloat(aValue.toString().replace(",", ".")) * 100f) / 100f;
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(UserControl.control.ui, "Der maximale Dispo hat kein gültiges Format! Bitte geben Sie einen Zahlenwert mit maximal zwei Nachkommastellen ein.", "Dispo falsch", JOptionPane.ERROR_MESSAGE);
                            super.setValueAt(old, row, column);
                            break;
                        }
                        accounts[row].setOverdraftFacility(dispo);
                        UserControl.control.updateAccount(accounts[row]);
                        break;
                }
            }
        };
        modelTableAccounts.addColumn("Konto");
        modelTableAccounts.addColumn("Kontotyp");
        modelTableAccounts.addColumn("Aktueller Kontostand");
        modelTableAccounts.addColumn("Maximaler Dispo");

        // Zeilen füllen
        users = UserControl.control.getRelatedUsers();
        ArrayList<BankAccount> a = new ArrayList<>();
        for( Person p : users) {
            modelTableUsers.addRow(new Object[]{
                    p.forename,
                    p.lastname,
                    p.role
            });

            BankAccount[] ac = UserControl.control.getBankAccounts(p);
            for (BankAccount b : ac) {
                float value = UserControl.control.getBankAccountValue(b);
                if (value < 0f) {
                    b.value = value;
                    a.add(b);
                }
            }
        }

        attentions = a.toArray(new BankAccount[]{});

        for (BankAccount b : attentions) {
            modelTableAttention.addRow(new Object[] {
                    b.owner.forename,
                    b.owner.lastname,
                    b.name,
                    b.value,
                    b.getOverdraftFacility()
                });
        }

        // Erstellen der JTable mit dem TableModel
        tableUsers = new JTable(modelTableUsers);
        tableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUsers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            /**
             * updates table accounts when the selection mode is not empty, clears the table accounts if empty
             * @param e the selection event
             */
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel model = tableUsers.getSelectionModel();
                if(! model.isSelectionEmpty()) {
                    refreshTableAccounts(model.getMinSelectionIndex());
                } else {
                    clearTableAccounts();
                }
            }
        });

        tableAttention = new JTable(modelTableAttention);
        tableAttention.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableAccounts = new JTable(modelTableAccounts);
        tableAccounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Anpassen der Zeilenhöhe
        tableUsers.setRowHeight(30);
        tableAttention.setRowHeight(30);
        tableAccounts.setRowHeight(30);

        // Automatische Anpassung der Spaltenbreiten
        tableUsers.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tableAttention.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tableAccounts.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // Value Renderer
        tableAttention.getColumnModel().getColumn(3).setCellRenderer(new AmountTableCellRenderer(3));
        tableAttention.getColumnModel().getColumn(4).setCellRenderer(new AmountTableCellRenderer(4));
        tableAccounts.getColumnModel().getColumn(2).setCellRenderer(new AmountTableCellRenderer(2));
        tableAccounts.getColumnModel().getColumn(3).setCellRenderer(new AmountTableCellRenderer(3));
    }

    /**
     * clears the Table Accounts
     */
    private void clearTableAccounts() {
        DefaultTableModel model = (DefaultTableModel) tableAccounts.getModel();

        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    /**
     * refreshes Table Accounts
     * @param minSelectionIndex the selected row
     */
    private void refreshTableAccounts(int minSelectionIndex) {
        DefaultTableModel model = (DefaultTableModel) tableAccounts.getModel();

        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }

        accounts = UserControl.control.getBankAccounts(users[minSelectionIndex]);
        for (BankAccount b : accounts) {
            model.addRow(new Object[] {
                    b.name,
                    b.getClass(),
                    UserControl.control.getBankAccountValue(b),
                    b.getOverdraftFacility()
                });
        }
    }
}
