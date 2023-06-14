package UI;

import Data.Person;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private JPanel panelMain;
    private JTable tableUsers;
    private JTable tableAccounts;
    private JTable tableAttention;

    public AdminDashboard() {
        setContentPane(panelMain);
    }

    private void createUIComponents() {
        // Erstellen der Tabelle
        DefaultTableModel modelTableUsers = new DefaultTableModel() {
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                Object oldValue = getValueAt(row,column);
                super.setValueAt(aValue, row, column);
            }
        };
        modelTableUsers.addColumn("Vorname");
        modelTableUsers.addColumn("Nachname");
        modelTableUsers.addColumn("Rolle");

        DefaultTableModel modelTableAttention = new DefaultTableModel();
        modelTableAttention.addColumn("Vorname");
        modelTableAttention.addColumn("Nachname");
        modelTableAttention.addColumn("Konto");
        modelTableAttention.addColumn("Aktueller Kontostand");
        modelTableAttention.addColumn("Maximaler Dispo");

        DefaultTableModel modelTableAccounts = new DefaultTableModel();
        modelTableAccounts.addColumn("Konto");
        modelTableAccounts.addColumn("Kontotyp");
        modelTableAccounts.addColumn("Aktueller Kontostand");
        modelTableAccounts.addColumn("Maximaler Dispo");

        modelTableUsers.addRow(new Object[] {"Max", UserControl.control.getUser(), "Arsch"});
        modelTableAttention.addRow(new Object[] {"Maximilian", "Mustermann", "Konto1", -12.5f, 30f});
        modelTableAccounts.addRow(new Object[] {"Konto1", "Giro", -12.5f, 30f});

        // Erstellen der JTable mit dem TableModel
        tableUsers = new JTable(modelTableUsers);
        tableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableAttention = new JTable(modelTableAttention);
        tableAttention.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableAccounts = new JTable(modelTableAttention);
        tableAccounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Anpassen der Zeilenhöhe
        tableUsers.setRowHeight(30);
        tableAttention.setRowHeight(30);
        tableAccounts.setRowHeight(30);

        // Automatische Anpassung der Spaltenbreiten
        tableUsers.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tableAttention.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tableAccounts.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // Value Renderer
        tableAttention.getColumnModel().getColumn(3).setCellRenderer(new AmountTableCellRenderer(3));
        tableAttention.getColumnModel().getColumn(4).setCellRenderer(new AmountTableCellRenderer(4));
        tableAccounts.getColumnModel().getColumn(4).setCellRenderer(new AmountTableCellRenderer(4));
        tableAccounts.getColumnModel().getColumn(3).setCellRenderer(new AmountTableCellRenderer(3));
        tableUsers.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Person) {
                    setText(((Person) value).lastname);
                }
                return component;
            }
        });
    }
}
