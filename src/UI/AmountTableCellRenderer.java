package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class AmountTableCellRenderer extends DefaultTableCellRenderer {

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00 €");
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 3 && value != null) {
            if((float)value < 0) {
                component.setForeground(Color.RED); // Beträge in rot darstellen
            } else {
                component.setForeground(table.getForeground()); // Standardfarbe für andere Zellen beibehalten
            }
            setHorizontalAlignment(SwingConstants.RIGHT); // Rechtsbündig ausrichten
            if (value instanceof Float) {
                value = decimalFormat.format(value); // Beträge im Format anzeigen
            }
            setText(value.toString());

        } else {
            component.setForeground(table.getForeground()); // Standardfarbe für andere Zellen beibehalten
        }
        return component;
    }
}
