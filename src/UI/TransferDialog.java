package UI;

import javax.swing.*;
import java.awt.event.*;

public class TransferDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxReciver;
    private JTextField textFieldTotal;
    private JTextArea textAreaPurpose;
    private JComboBox comboBoxAccount;

    public TransferDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Überweisung");
        setIconImage(new ImageIcon("src/assets/payment.png").getImage());

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
        setVisible(true);
    }

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

        String receiver = (comboBoxReciver.getSelectedItem().toString());
        String account = (comboBoxAccount.getSelectedItem().toString());

        int userDecision = JOptionPane.showConfirmDialog(this, "<html>Bestellung prüfen:<br><table>" +
                "<tr><tb>Konto:</tb><tb>"+account+"</tb></tr>" +
                "<tr><tb>Empfänger:</tb><tb>"+receiver+"</tb></tr>"+
                "<tr><tb>Betrag:</tb><tb>"+total+"€</tb></tr>"+
                "<tr><tb>Verwendungszweck:</tb><tb>"+textAreaPurpose.getText()+"</tb></tr>"+
                "</table></html>");

        if(userDecision != JOptionPane.OK_OPTION) return;

        UserControl.control.transferMoney(
                account,
                receiver,
                total,
                textAreaPurpose.getText()
            );

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        TransferDialog dialog = new TransferDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
