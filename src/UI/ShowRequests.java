package UI;

import Data.Person;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;

/**
 * Displays a dialog to view all pending friend requests.
 * @author Egzon Zenuni
 * @version v1.0_stable_alpha
 */
public class ShowRequests extends JDialog {
    /**
     * Panel containing the content
     */
    private JPanel contentPane;
    /**
     * ok button
     */
    private JButton buttonOK;
    /**
     * Table containing all pending friend requests
     */
    private JTable table1;

    /**
     * Shows the dialog
     */
    public ShowRequests() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        Program.setIcon(this, "businessman");
        setTitle("Ausstehende Anfragen");



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

        // call onCancel() on ENTER
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
    }

    /**
     * Close the dialog
     */
    private void onOK() {
        // add your code here
        dispose();
    }

    /**
     * Creates the ui components and is called before the object (ShowRequests) is instantiated
     */
    private void createUIComponents() {
        // Erstellen der Tabelle
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column==3 || column == 4; // Alle Zellen als nicht editierbar markieren
            }
        };
        model.addColumn("Vorname");
        model.addColumn("Nachname");
        model.addColumn("Rolle");
        model.addColumn("Annehmen");
        model.addColumn("Ablehnen");

        for (Person p : UserControl.control.getPendingRequests()) {
            model.addRow(new Object[]{
                    p.forename,
                    p.lastname,
                    p.role,
                    "Annehmen",
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
                String f = table.getValueAt(row, 0).toString();
                String l = table.getValueAt(row, 1).toString();
                UserControl.control.acceptFriend(f, l);
                int rowCount = table1.getModel().getRowCount();
                for (int i = rowCount - 1; i >= 0; i--) {
                    ((DefaultTableModel)table1.getModel()).removeRow(i);
                }
                JOptionPane.showMessageDialog(button, f + " " + l + " wurde als Kontakt angenommen.");
                for (Person p : UserControl.control.getPendingRequests()) {
                    ((DefaultTableModel)table1.getModel()).addRow(new Object[]{
                            p.forename,
                            p.lastname,
                            p.role,
                            "Annehmen",
                            "Ablehnen"
                    });
                }
            }
        });
        table1.getColumnModel().getColumn(4).setCellRenderer((TableCellRenderer) new ClientsTableButtonRenderer());
        table1.getColumnModel().getColumn(4).setCellEditor(new ClientsTableRenderer(new JCheckBox()) {
            @Override
            public void onClick(ClientsTableRenderer clientsTableRenderer) {
                String f = table.getValueAt(row, 0).toString();
                String l = table.getValueAt(row, 1).toString();
                UserControl.control.rejectFriend(f, l);
                int rowCount = table1.getModel().getRowCount();
                for (int i = rowCount - 1; i >= 0; i--) {
                    ((DefaultTableModel)table1.getModel()).removeRow(i);
                }
                JOptionPane.showMessageDialog(button, f + " " + l + " wurde als Kontakt abgelehnt.");
                for (Person p : UserControl.control.getPendingRequests()) {
                    ((DefaultTableModel)table1.getModel()).addRow(new Object[]{
                            p.forename,
                            p.lastname,
                            p.role,
                            "Annehmen",
                            "Ablehnen"
                    });
                }
            }
        });
        table1.setPreferredScrollableViewportSize(table1.getPreferredSize());
        table1.setShowHorizontalLines(true);
        table1.setShowVerticalLines(false);
    }
}
