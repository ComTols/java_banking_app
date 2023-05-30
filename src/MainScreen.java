import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

public class MainScreen extends JFrame {
    private JPanel panelMain;
    private JComboBox comboBoxAccount;
    private JTable table1;
    private JScrollPane scrollPane;
    private JButton btnAccountinfo;

    public MainScreen() {
        setContentPane(panelMain);
        UserControl.control.ui = this;
        createAndShowGui();
        comboBoxAccount.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(comboBoxAccount.getSelectedIndex());
            }
        });
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

        // Hinzufügen von Beispieldaten
        model.addRow(new Object[]{"2023-05-01", "Max Mustermann", "Miete", 100.0});
        model.addRow(new Object[]{"2023-05-02", "Erika Musterfrau", "Einkauf", 200.0});
        model.addRow(new Object[]{"2023-05-03", "Hans Beispiel", "Gehalt", -150.0});
        model.addRow(new Object[]{"2023-05-04", "Anna Test", "Rechnung", 300.0});
        model.addRow(new Object[]{"2023-05-05", "Peter Proband", "Kredit", 250.0});

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
                    if((double)value < 0) {
                        component.setForeground(Color.RED); // Beträge in rot darstellen
                    } else {
                        component.setForeground(table.getForeground()); // Standardfarbe für andere Zellen beibehalten
                    }
                    setHorizontalAlignment(SwingConstants.RIGHT); // Rechtsbündig ausrichten
                    if (value instanceof Double) {
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
