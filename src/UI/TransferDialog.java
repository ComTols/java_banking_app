package UI;

import Data.BankAccount;
import Data.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Displays a dialog to make a transaction to a friend user.
 * @author Joscha Dierks
 * @version v1.0_stable_alpha
 */
public class TransferDialog extends JDialog implements ISelectReceiver {
    /**
     * Panel containing the content
     */
    private JPanel contentPane;
    /**
     * ok button
     */
    private JButton buttonOK;
    /**
     * cancel button
     */
    private JButton buttonCancel;
    /**
     * text field containing amount
     */
    private JTextField textFieldTotal;
    /**
     * text area containing description
     */
    private JTextArea textAreaPurpose;
    /**
     * List with all available accounts to select sender bank account
     */
    private JComboBox comboBoxAccount;
    /**
     * list containing all receiving persons
     */
    private JList listReceiver;
    /**
     * add button
     */
    private JButton btnAdd;
    /**
     * delete button
     */
    private JButton btnDelete;

    /**
     * Show the dialog
     */
    public TransferDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Überweisung");
        Program.setIcon(this, "payment");

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectContacts();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel model = (DefaultListModel) listReceiver.getModel();
                int selectedIndex = listReceiver.getSelectedIndex();
                if (selectedIndex != -1) {
                    model.remove(selectedIndex);
                }
            }
        });

        for (BankAccount b : UserControl.control.getBankAccounts()) {
            comboBoxAccount.addItem(b);
        }

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

    /**
     * Validate the input and transfer the money
     */
    private void onOK() {

        if(textAreaPurpose.getText().isEmpty()) {
            textAreaPurpose.setText("Überweisung");
        }

        if(textFieldTotal.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Der Betrag darf nicht leer sein!", "Betrag angeben", JOptionPane.ERROR_MESSAGE);
            return;
        }

        textFieldTotal.setText(textFieldTotal.getText().replace(",", "."));
        float total = -1.0f;
        try {
            total = Float.parseFloat(textFieldTotal.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Der Betrag hat kein gültiges Format! Bitte geben Sie einen Zahlenwert mit maximal zwei Nachkommastellen ein.", "Betrag falsch", JOptionPane.ERROR_MESSAGE);
            return;
        }

        total = Math.round(total * 100f) / 100f;

        if(total <= 0) {
            JOptionPane.showMessageDialog(this, "Der Betrag muss positiv sein! Bitte geben Sie einen Zahlenwert mit maximal zwei Nachkommastellen ein, der größer als 0 ist.", "Betrag falsch", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(listReceiver.getModel().getSize() <= 0) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie mindestens einen Empfänger aus!", "Empfänger auswählen", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Person> receivers = new ArrayList<>();
        StringBuilder receiver = new StringBuilder();

        for (int i = 0; i < listReceiver.getModel().getSize(); i ++) {
            Person p = (Person) listReceiver.getModel().getElementAt(i);
            receivers.add(p);
            receiver.append("\"");
            receiver.append(p.toString());
            receiver.append("\"; ");
        }
        receiver.deleteCharAt(receiver.length() - 1);
        receiver.deleteCharAt(receiver.length() - 1);


        BankAccount account = (BankAccount) comboBoxAccount.getSelectedItem();

        int userDecision = JOptionPane.showConfirmDialog(this, "<html>Überweisung prüfen:<br><table>" +
                "<tr><tb>Konto:</tb><tb>"+account+"</tb></tr>" +
                "<tr><tb>Empfänger:</tb><tb>"+receiver+"</tb></tr>"+
                "<tr><tb>Betrag:</tb><tb>"+total+"€</tb></tr>"+
                "<tr><tb>Verwendungszweck:</tb><tb>"+textAreaPurpose.getText()+"</tb></tr>"+
                "</table></html>");

        if(userDecision != JOptionPane.OK_OPTION) return;

        UserControl.control.transferMoney(
                account,
                receivers.toArray(new Person[0]),
                total,
                textAreaPurpose.getText()
            );

        UserControl.control.ui.refreshTransactions();
        dispose();
    }

    /**
     * Close the dialog
     */
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    /**
     * Shows a dialog to select contacts
     */
    private void selectContacts() {
        new SelectContacts(this, false);
    }

    @Override
    public void receiveSelectedContacts(Person[] contacts) {
        DefaultListModel model = (DefaultListModel) listReceiver.getModel();
        model.clear();
        for(Person p : contacts) {
            model.addElement(p);
        }
    }
}
