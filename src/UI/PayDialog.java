package UI;

import Data.BankAccount;
import Data.PayRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 * Displays a dialog to pay an invoice.
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class PayDialog extends JDialog {
    /**
     * the content pane containing the pay dialog
     */
    private JPanel contentPane;
    /**
     * the OK-button
     */
    private JButton buttonOK;
    /**
     * table containg all the bills of the user
     */
    private JTable table1;
    /**
     * list with all selectable bank-accounts
     */
    private JComboBox comboBox1;

    public PayDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        Program.setIcon(this, "wallet");
        setTitle("Ausstehende Rechnungen");

        for (BankAccount b : UserControl.control.getBankAccounts()) {
            comboBox1.addItem(b);
        }

        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
    }

    /**
     * closes window
     */
    private void onOK() {
        // add your code here
        dispose();
    }

    /**
     * creates the UI-components and is called before the object (paydialog) is instantiated
     */
    private void createUIComponents() {
        // Erstellen der Tabelle
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column==3 || column == 4; // Alle Zellen als nicht editierbar markieren
            }
        };

        model.addColumn("Von");
        model.addColumn("Verwendungszweck");
        model.addColumn("Betrag");
        model.addColumn("Bezahlen");
        model.addColumn("Ablehnen");

        for (PayRequest p : UserControl.control.getPayRequests()) {
            model.addRow(new Object[] {
                    p,
                    p.purpose,
                    p.total,
                    "Bezahlen",
                    "Ablehnen"
            });
        }

        // Erstellen der JTable mit dem TableModel
        table1 = new JTable(model);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowHeight(30);

        table1.getColumnModel().getColumn(3).setCellRenderer(new ClientsTableButtonRenderer());
        table1.getColumnModel().getColumn(3).setCellEditor(new ClientsTableRenderer(new JCheckBox()) {

            @Override
            public void onClick(ClientsTableRenderer clientsTableRenderer) {
                PayRequest payRequest = (PayRequest) table1.getValueAt(row, 0);
                int choice = JOptionPane.showConfirmDialog(null, "Möchten Sie " + payRequest.total + " an " + payRequest.from.owner + " bezahlen?", "Überweisung bestätigen",JOptionPane.OK_CANCEL_OPTION);
                if (choice == JOptionPane.OK_OPTION) {
                    UserControl.control.pay(payRequest, (BankAccount)comboBox1.getSelectedItem());
                    refreshPays();
                }
            }
        });
        table1.getColumnModel().getColumn(4).setCellRenderer(new ClientsTableButtonRenderer());
        table1.getColumnModel().getColumn(4).setCellEditor(new ClientsTableRenderer(new JCheckBox()) {
            @Override
            public void onClick(ClientsTableRenderer clientsTableRenderer) {
                PayRequest payRequest = (PayRequest) table1.getValueAt(row, 0);
                int choice = JOptionPane.showConfirmDialog(null, "Möchten Sie die Anfrage von " + payRequest.from.owner + " wirklich löschen?", "Rechnung ablehnen",JOptionPane.OK_CANCEL_OPTION);
                if (choice == JOptionPane.OK_OPTION) {
                    UserControl.control.deletePayRequest(payRequest);
                    refreshPays();
                }
            }
        });
        table1.setPreferredScrollableViewportSize(table1.getPreferredSize());
        table1.setShowHorizontalLines(true);
        table1.setShowVerticalLines(false);

        // Beträge in rot darstellen
        DefaultTableCellRenderer renderer = new AmountTableCellRenderer(2);

        table1.getColumnModel().getColumn(2).setCellRenderer(renderer);
    }

    /**
     * refrehes the payments-table
     */
    private void refreshPays() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        int rowCount = table1.getModel().getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        for (PayRequest p : UserControl.control.getPayRequests()) {
            model.addRow(new Object[] {
                    p,
                    p.purpose,
                    p.total,
                    "Bezahlen",
                    "Ablehnen"
            });
        }
    }
}
