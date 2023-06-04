package UI;

import Data.BankAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MoveMoneyDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxFrom;
    private JComboBox comboBoxTo;
    private JTextField textFieldTotal;
    private JTextArea textAreaPurpose;

    public MoveMoneyDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setIconImage(new ImageIcon("src/assets/gold.png").getImage());
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

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
