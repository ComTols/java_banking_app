import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.event.*;

public class ShowRequests extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTable table1;

    public ShowRequests() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setIconImage(new ImageIcon("src/assets/businessman.png").getImage());
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
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void main(String[] args) {
        ShowRequests dialog = new ShowRequests();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

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

        // Hinzufügen von Beispieldaten
        model.addRow(new Object[]{"Max", "Mustermann", "Kunde", "Annehmen", "Ablehnen"});
        model.addRow(new Object[]{"Erika", "Musterfrau", "Kunde", "Annehmen", "Ablehnen"});
        model.addRow(new Object[]{"Hans", "Beispiel", "Bänker", "Annehmen", "Ablehnen"});
        model.addRow(new Object[]{"Anna", "Test", "Bänker", "Annehmen", "Ablehnen"});
        model.addRow(new Object[]{"Peter", "Proband", "Kurde", "Annehmen", "Ablehnen"});

        // Erstellen der JTable mit dem TableModel
        table1 = new JTable(model);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowHeight(30);

        table1.getColumnModel().getColumn(3).setCellRenderer((TableCellRenderer) new ClientsTableButtonRenderer());
        table1.getColumnModel().getColumn(3).setCellEditor(new ClientsTableRenderer(new JCheckBox()) {
            @Override
            public void onClick(ClientsTableRenderer clientsTableRenderer) {
                UserControl.control.acceptFriend((String) table.getValueAt(row, 0), (String) table.getValueAt(row, 1));
                JOptionPane.showMessageDialog(button, table.getValueAt(row, 0) + " " + table.getValueAt(row, 1) + " wurde als Kontakt angenommen.");
            }
        });
        table1.getColumnModel().getColumn(4).setCellRenderer((TableCellRenderer) new ClientsTableButtonRenderer());
        table1.getColumnModel().getColumn(4).setCellEditor(new ClientsTableRenderer(new JCheckBox()) {
            @Override
            public void onClick(ClientsTableRenderer clientsTableRenderer) {
                UserControl.control.rejectFriend((String) table.getValueAt(row, 0), (String) table.getValueAt(row, 1));
                JOptionPane.showMessageDialog(button, table.getValueAt(row, 0) + " " + table.getValueAt(row, 1) + " wurde als Kontakt abgelehnt.");
            }
        });
        table1.setPreferredScrollableViewportSize(table1.getPreferredSize());
        table1.setShowHorizontalLines(true);
        table1.setShowVerticalLines(false);
    }
}