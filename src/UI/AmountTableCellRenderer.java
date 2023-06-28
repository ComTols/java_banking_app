package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Represents a formatted amount in the table.
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class AmountTableCellRenderer extends DefaultTableCellRenderer {

    /**
     * Column number (start with 0) witch should be effected
     */
    private int column;
    /**
     * Format how the digits should be presented
     */
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00 €");

    /**
     * Instantiate new Renderer
     * @param column Column number (start with 0) witch should be effected
     */
    public AmountTableCellRenderer(int column) {
        this.column = column;
    }

    /**
     * Renders the amount as a beautiful red or black formatted number
     *
     * @param table  the <code>JTable</code>
     * @param value  the value to assign to the cell at
     *                  <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == this.column && value != null) {
            try {
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
            } catch (ClassCastException e) {
            }

        } else {
            component.setForeground(table.getForeground()); // Standardfarbe für andere Zellen beibehalten
        }
        return component;
    }
}
