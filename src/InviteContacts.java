import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.event.*;

public class
InviteContacts extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTable table1;
    private JScrollBar scrollBar1;

    public InviteContacts() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Kontakte anzeigen");
        setIconImage(new ImageIcon("src/assets/contacts.png").getImage());


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

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
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

        // Hinzufügen von Beispieldaten
        model.addRow(new Object[]{"Max", "Mustermann", "Kunde", "Hinzufügen"});
        model.addRow(new Object[]{"Erika", "Musterfrau", "Kunde", "Hinzufügen"});
        model.addRow(new Object[]{"Hans", "Beispiel", "Bänker", "Entfernen"});
        model.addRow(new Object[]{"Anna", "Test", "Bänker", "Hinzufügen"});
        model.addRow(new Object[]{"Peter", "Proband", "Kurde", "Entfernen"});

        // Erstellen der JTable mit dem TableModel
        table1 = new JTable(model);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowHeight(30);

        table1.getColumnModel().getColumn(3).setCellRenderer((TableCellRenderer) new ClientsTableButtonRenderer());
        table1.getColumnModel().getColumn(3).setCellEditor(new ClientsTableRenderer(new JCheckBox()) {
            @Override
            public void onClick(ClientsTableRenderer clientsTableRenderer) {
                if (table.getValueAt(row, 3).equals("Hinzufügen")) {
                    UserControl.control.addFriend((String) table.getValueAt(row, 0), (String) table.getValueAt(row, 1));
                    JOptionPane.showMessageDialog(button, table.getValueAt(row, 0) + " " + table.getValueAt(row, 1) + " wurde als Kontakt hinzugefügt.");
                } else {
                    UserControl.control.removeFriend((String) table.getValueAt(row, 0), (String) table.getValueAt(row, 1));
                    JOptionPane.showMessageDialog(button, table.getValueAt(row, 0) + " " + table.getValueAt(row, 1) + " wurde als Kontakt entfernt.");
                }
            }
        });
        table1.setPreferredScrollableViewportSize(table1.getPreferredSize());
        table1.setShowHorizontalLines(true);
        table1.setShowVerticalLines(false);
    }
}

