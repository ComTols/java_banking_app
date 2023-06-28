package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Makes a button cell in a table clickable and triggers the {@link #onClick(ClientsTableRenderer)} function
 */
public abstract class ClientsTableRenderer extends DefaultCellEditor {
    /**
     * The button to be showen in the table
     */
    JButton button;
    /**
     * The text in the button
     */
    private String label;
    /**
     * True, if button is clicked
     */
    private boolean clicked;
    /**
     * The effected row in the table
     */
    int row;
    /**
     * The effected column in the table
     */
    private int col;
    /**
     * The related table
     */
    JTable table;

    /**
     * Instantiates the table renderer
     * @param checkBox check box to catch events
     */
    public ClientsTableRenderer(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    /**
     * Implements the TableCellEditor interface.
     * @param table           the <code>JTable</code> that is asking the
     *                          editor to edit; can be <code>null</code>
     * @param value           the value of the cell to be edited; it is
     *                          up to the specific editor to interpret
     *                          and draw the value.  For example, if value is
     *                          the string "true", it could be rendered as a
     *                          string or it could be rendered as a check
     *                          box that is checked.  <code>null</code>
     *                          is a valid value
     * @param isSelected      true if the cell is to be rendered with
     *                          highlighting
     * @param row             the row of the cell being edited
     * @param column          the column of the cell being edited
     * @return
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        this.col = column;

        button.setForeground(Color.black);
        button.setBackground(UIManager.getColor("Button.background"));
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        clicked = true;
        return button;
    }

    /**
     * Get the cell editor value as a string
     * @return the value
     */
    public Object getCellEditorValue() {
        if (clicked) {
            onClick(this);
        }
        clicked = false;
        return new String(label);
    }

    /**
     * Called when rendering should finish
     * @return true if editing was stopped; false otherwise
     */
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type. The event instance is created lazily.
     */
    protected void fireEditingStopped() {
        try {
            super.fireEditingStopped();
        } catch (Exception e) {

        }
    }

    /**
     * Triggered when the button was pressed
     * @param clientsTableRenderer the clicked button
     */
    public abstract void onClick(ClientsTableRenderer clientsTableRenderer);
}
