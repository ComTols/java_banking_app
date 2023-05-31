package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ClientsTableRenderer extends DefaultCellEditor {
    JButton button;
    private String label;
    private boolean clicked;
    int row;
    private int col;
    JTable table;

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

    public Object getCellEditorValue() {
        if (clicked) {
            onClick(this);
        }
        clicked = false;
        return new String(label);
    }

    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

    public abstract void onClick(ClientsTableRenderer clientsTableRenderer);
}
