package UI;

import Data.Person;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class
InviteContacts extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTable table1;

    public InviteContacts() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Freunde einladen");
        Program.setIcon(this, "contacts");


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

        contentPane.registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void main(String[] args) {
        InviteContacts dialog = new InviteContacts();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

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
        model.addColumn("Hinzufügen");

        for (Person p : UserControl.control.getAvailableFriends()) {
            model.addRow(new Object[]{
                    p.forename,
                    p.lastname,
                    p.role,
                    "Hinzufügen"
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
                String f = table.getValueAt(row, 0).toString();
                String l = table.getValueAt(row, 1).toString();
                UserControl.control.addFriend(f, l);
                int rowCount = table1.getModel().getRowCount();
                for (int i = rowCount - 1; i >= 0; i--) {
                    ((DefaultTableModel)table1.getModel()).removeRow(i);
                }
                JOptionPane.showMessageDialog(button, f + " " + l + " wurde als Kontakt hinzugefügt.");
                for (Person p : UserControl.control.getAvailableFriends()) {
                    ((DefaultTableModel)table1.getModel()).addRow(new Object[]{
                            p.forename,
                            p.lastname,
                            p.role,
                            "Hinzufügen"
                    });
                }
            }
        });
        table1.setPreferredScrollableViewportSize(table1.getPreferredSize());
        table1.setShowHorizontalLines(true);
        table1.setShowVerticalLines(false);
    }
}


