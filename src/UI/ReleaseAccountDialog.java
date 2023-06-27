package UI;

import Data.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReleaseAccountDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTable table1;
    private BankAccount[] pendingAccounts;

    public ReleaseAccountDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        Program.setIcon(this, "law");
        setTitle("Konten freigeben");

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

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
                return column==8 || column==9 || column==0 || column==4; // Alle Zellen als nicht editierbar markieren
            }
        };
        model.addColumn("Name");
        model.addColumn("Vorname");
        model.addColumn("Nachname");
        model.addColumn("Typ");
        model.addColumn("Dispo");
        model.addColumn("Zugriffsdatum");
        model.addColumn("Zweitbesitzer");
        model.addColumn("Referenzkonto");
        model.addColumn("Zulassen");
        model.addColumn("Ablehnen");

        refreshTable(model);


        // Erstellen der JTable mit dem TableModel
        table1 = new JTable(model);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Anpassen der Zeilenhöhe
        table1.setRowHeight(30);

        table1.getColumnModel().getColumn(4).setCellRenderer(new AmountTableCellRenderer(4));

        table1.getColumnModel().getColumn(8).setCellRenderer((TableCellRenderer) new ClientsTableButtonRenderer());
        table1.getColumnModel().getColumn(8).setCellEditor(new ClientsTableRenderer(new JCheckBox()) {
            @Override
            public void onClick(ClientsTableRenderer clientsTableRenderer) {
                UserControl.control.acceptBankAccount(pendingAccounts[row]);
                refreshTable((DefaultTableModel) table1.getModel());
            }
        });

        table1.getColumnModel().getColumn(9).setCellRenderer((TableCellRenderer) new ClientsTableButtonRenderer());
        table1.getColumnModel().getColumn(9).setCellEditor(new ClientsTableRenderer(new JCheckBox()) {
            @Override
            public void onClick(ClientsTableRenderer clientsTableRenderer) {
                UserControl.control.rejectBankAccount(pendingAccounts[row]);
                refreshTable((DefaultTableModel) table1.getModel());
            }
        });
    }

    private void refreshTable(DefaultTableModel model) {

        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }

        pendingAccounts = UserControl.control.getPendingAccounts();
        for (BankAccount b : pendingAccounts) {
            Object[] row = new Object[] {
                    b.name,
                    b.owner.forename,
                    b.owner.lastname,
                    b.getClass(),
                    b.getOverdraftFacility(),
                    "-", "-", "-", "Zulassen", "Ablehnen"
            };
            if(b instanceof FixedDepositAccount) {
                row[5] = ((FixedDepositAccount) b).accessDate;
            }
            if (b instanceof SharedAccount) {
                row[6] = ((SharedAccount) b).secondOwner;
            }
            if(b instanceof SavingAccount) {
                row[7] = ((SavingAccount) b).reference;
            }
            if (b instanceof CreditAccount) {
                row[7] = ((CreditAccount) b).referenceAccount;
            }
            model.addRow(row);
        }

        UserControl.control.ui.refreshBankAccounts();
    }
}
