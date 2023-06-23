package UI;

import Data.BankAccount;
import Data.PayRequest;
import Data.Person;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class PayDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTable table1;
    private JComboBox comboBox1;

    public PayDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        Program.setIcon(this, "wallet");
        setTitle("Ausstehende Anfragen");

        for (BankAccount b : UserControl.control.getBankAccounts()) {
            comboBox1.addItem(b);
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

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

        table1.getColumnModel().getColumn(3).setCellRenderer((TableCellRenderer) new ClientsTableButtonRenderer());
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
        table1.getColumnModel().getColumn(4).setCellRenderer((TableCellRenderer) new ClientsTableButtonRenderer());
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
