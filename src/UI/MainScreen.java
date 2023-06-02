package UI;

import Data.BankAccount;
import Data.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

public class MainScreen extends JFrame {
    private JPanel panelMain;
    private JComboBox comboBoxAccount;
    private JTable table1;
    private JScrollPane scrollPane;
    private JButton btnAccountinfo;
    private JLabel labelTotal;
    private JButton btnDelete;
    private float total = 0f;

    public MainScreen() {
        setContentPane(panelMain);
        UserControl.control.ui = this;
        createAndShowGui();

        refreshBankAccounts();

        comboBoxAccount.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                UserControl.control.setActiveAccount((BankAccount) comboBoxAccount.getSelectedItem());
                refreshTransactions();
            }
        });

        btnAccountinfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBankAccountInfos();
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAccount();
            }
        });
    }

    private void deleteAccount() {
        if(total < 0) {
            JOptionPane.showMessageDialog(this, "Sie müssen zuerst das Konto ausgleichen!", "Kontostand negativ", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (UserControl.control.getUser().mainAccountName.equals(((BankAccount)comboBoxAccount.getSelectedItem()).name)) {
            JOptionPane.showMessageDialog(this, "Sie können Ihr Hauptkonto nicht löschen!", "Hauptkonto", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (total > 0) {
            // TODO: tranfer money to main account
        }
        UserControl.control.deleteAccount((BankAccount)comboBoxAccount.getSelectedItem());
        refreshBankAccounts();
    }

    private void showBankAccountInfos() {
        BankAccount b = UserControl.control.getActiveAccount();
        JOptionPane.showMessageDialog(this,
                "<html>Dieses Konto:<br>Name: " + b + "<br>Typ: "+ b.getClass() +"<br>Maximaler Dispokredit: "+b.getOverdraftFacility()+"€</html>", "Kontoinformationen", JOptionPane.INFORMATION_MESSAGE);
    }

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
        } catch (NullPointerException e) {}
        refreshTransactions();
    }

    public void refreshTransactions() {

        float totalAccount = 0.0f;

        int rowCount = table1.getModel().getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            ((DefaultTableModel)table1.getModel()).removeRow(i);
        }
        try {
            for (Transaction t : UserControl.control.getTransactions()) {
                String sender = "";
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
        } catch (NullPointerException e) {}

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00 €");

        labelTotal.setText("Kontostand: " + decimalFormat.format(totalAccount));
        total = totalAccount;
    }

    private void createAndShowGui() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(new MainScreenMenu());
        setSize(400, 350);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Bank");
        setIconImage(new ImageIcon("src/assets/money-coins.png").getImage());
        setLocationByPlatform(true);
        setVisible(true);
        new LoginDialog();
    }

    private void createUIComponents() {
        // Erstellen der Tabelle
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Alle Zellen als nicht editierbar markieren
            }
        };
        model.addColumn("Datum");
        model.addColumn("Absender");
        model.addColumn("Verwendungszweck");
        model.addColumn("Betrag");

        model.setRowCount(100);

        // Erstellen der JTable mit dem TableModel
        table1 = new JTable(model);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowHeight(30);

        // Anpassen der Spaltenbreite
        table1.getColumnModel().getColumn(0).setPreferredWidth(90);
        table1.getColumnModel().getColumn(1).setPreferredWidth(120);
        table1.getColumnModel().getColumn(3).setPreferredWidth(90);

        // Anpassen der Zeilenhöhe
        table1.setRowHeight(30);

        // Automatische Anpassung der Spaltenbreiten
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table1.getColumnModel().getColumn(2).setPreferredWidth(1000);

        // Beträge in rot darstellen
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00 €");
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 3 && value != null) {
                    if((float)value < 0) {
                        component.setForeground(Color.RED); // Beträge in rot darstellen
                    } else {
                        component.setForeground(table.getForeground()); // Standardfarbe für andere Zellen beibehalten
                    }
                    setHorizontalAlignment(SwingConstants.RIGHT); // Rechtsbündig ausrichten
                    if (value instanceof Float) {
                        value = decimalFormat.format(value); // Beträge im Format anzeigen
                    }
                    setText(value.toString());

                } else {
                    component.setForeground(table.getForeground()); // Standardfarbe für andere Zellen beibehalten
                }
                return component;
            }
        };

        table1.getColumnModel().getColumn(3).setCellRenderer(renderer);
    }


}
