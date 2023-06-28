package UI;

import Data.BankAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Shows a dialog to move money to one of your own accounts.
 * @author fynn thierling
 * @version v1.0_stable_alpha
 */
public class MoveMoneyDialog extends JDialog {
    /**
     * the panel containing the content to move the money
     */
    private JPanel contentPane;
    /**
     * the OK-Button
     */
    private JButton buttonOK;
    /**
     * the Cancel-Button
     */
    private JButton buttonCancel;
    /**
     * the combobox used for the selected receiving account
     */
    private JComboBox comboBoxFrom;
    /**
     * the combobox used for the selected sending account
     */
    private JComboBox comboBoxTo;
    /**
     * the textfield used for the sent amount
     */
    private JTextField textFieldTotal;
    /**
     * the textfield used for the purpose
     */
    private JTextArea textAreaPurpose;

    /**
     * moves money values between your own bank-accounts
     */
    public MoveMoneyDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        Program.setIcon(this, "gold");
        setTitle("Geld verschieben");

        for (BankAccount b : UserControl.control.getBankAccounts()) {
            comboBoxTo.addItem(b);
            comboBoxFrom.addItem(b);
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

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
    }

    /**
     * moves the money between the selected accounts
     */
    private void onOK() {
        BankAccount from = (BankAccount) comboBoxFrom.getSelectedItem();
        BankAccount to = (BankAccount) comboBoxTo.getSelectedItem();
        if(from == to) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie zwei verschiedene Konten aus!", "Konten identisch", JOptionPane.ERROR_MESSAGE);
            return;
        }
        textFieldTotal.setText(textFieldTotal.getText().replace(",", "."));
        float total = 0f;
        try {
            total = Float.parseFloat(textFieldTotal.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie einen gültigen Betrag an!", "Betrag inkorrekt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        total = Math.round(total * 100f) / 100f;
        if (total <= 0) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie einen positiven Betrag an!", "Betrag zu klein", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UserControl.control.moveMoney(from, to, total, textAreaPurpose.getText());
        dispose();
    }

    /**
     * closes the window
     */
    private void onCancel() {
        dispose();
    }
}
