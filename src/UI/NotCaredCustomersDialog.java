package UI;

import Data.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays a dialog with a list of users who have not yet been assigned an admin.
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class NotCaredCustomersDialog extends JDialog {
    /**
     * Panel containing the content
     */
    private JPanel contentPane;
    /**
     * ok button
     */
    private JButton buttonOK;
    /**
     * Table containing all not cared users
     */
    private JTable table1;
    /**
     * all not cared users ordered like the table
     */
    private Person[] users;

    /**
     * Shows the dialog
     */
    public NotCaredCustomersDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        Program.setIcon(this, "hierarchy");
        setTitle("Kunden zuweisen");

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
    }

    /**
     * Clos the dialog
     */
    private void onOK() {
        // add your code here
        dispose();
    }

    /**
     * Creates the ui components and is called before the object (NotCaredCustomersDialog) is instantiated
     */
    private void createUIComponents() {
        // Erstellen der Tabelle
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column==3; // Alle Zellen als nicht editierbar markieren
            }
        };
        model.addColumn("Vorname");
        model.addColumn("Nachname");
        model.addColumn("Rolle");
        model.addColumn("Zuweisen");

        users = UserControl.control.getNotCaredCustomers();
        for (Person p : users) {
            model.addRow(new Object[] {
                    p.forename,
                    p.lastname,
                    p.role,
                    "Zuweisen"
            });
        }


        // Erstellen der JTable mit dem TableModel
        table1 = new JTable(model);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Anpassen der Zeilenhöhe
        table1.setRowHeight(30);

        table1.getColumnModel().getColumn(3).setCellRenderer((TableCellRenderer) new ClientsTableButtonRenderer());
        table1.getColumnModel().getColumn(3).setCellEditor(new ClientsTableRenderer(new JCheckBox()) {
            @Override
            public void onClick(ClientsTableRenderer clientsTableRenderer) {
                UserControl.control.careForCustomer(users[row]);
                update();
            }
        });
    }

    /**
     * Reloads the table content
     */
    private void update() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();

        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }

        users = UserControl.control.getNotCaredCustomers();
        for (Person p : users) {
            model.addRow(new Object[] {
                    p.forename,
                    p.lastname,
                    p.role,
                    "Zuweisen"
            });
        }
    }
}
