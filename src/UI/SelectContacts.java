package UI;

import Data.Person;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SelectContacts extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable table1;
    private ISelectReceiver receiver;
    private boolean selfIncluded;

    public SelectContacts(ISelectReceiver receiver, boolean selfIncluded) {
        this.selfIncluded = selfIncluded;
        this.receiver = receiver;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        Program.setIcon(this, "contacts");
        setTitle("Kontakte auswählen");


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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

    private void onOK() {
        ArrayList<Person> persons = new ArrayList<>();
        for (int i = 0; i < table1.getModel().getRowCount(); i++) {
            boolean sel = (boolean) table1.getModel().getValueAt(i, 3);
            if (sel) {
                persons.add((Person) table1.getModel().getValueAt(i, 1));
            }
        }

        receiver.receiveSelectedContacts(persons.toArray(new Person[0]));
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
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
        model.addColumn("Auswählen");

        for (Person p : UserControl.control.getContacts()) {
            model.addRow(new Object[]{
                    p.forename,
                    p,
                    p.role,
                    false
            });
        }

        if (selfIncluded) {
            model.addRow(new Object[]{
                    UserControl.control.getUser().forename,
                    UserControl.control.getUser(),
                    UserControl.control.getUser().role,
                    false
            });
        }

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Person) {
                    setText(((Person) value).lastname);
                }
                return component;
            }
        };

        // Erstellen der JTable mit dem TableModel
        table1 = new JTable(model) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 3:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        };
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowHeight(30);

        table1.setPreferredScrollableViewportSize(table1.getPreferredSize());
        table1.setShowHorizontalLines(true);
        table1.setShowVerticalLines(false);
        table1.getColumnModel().getColumn(1).setCellRenderer(renderer);
    }
}
