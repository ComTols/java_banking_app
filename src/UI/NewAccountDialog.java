package UI;

import CustomExceptions.DuplicateKeyException;
import Data.BankAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class NewAccountDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton girokontoRadioButton;
    private JRadioButton fixedDepositAccountRadioButton;
    private JRadioButton creditAccountRadioButton;
    private JRadioButton savingsAccountRadioButton;
    private JTextField textFieldDispo;
    private JButton infoButton;
    private JTextField textFieldName;
    private JRadioButton sharedAccountRadioButton;
    private ButtonGroup buttonGroupAccountType;

    public NewAccountDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(200, -1);
        setTitle("Konto erstellen");
        Program.setIcon(this, "money-bag");


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

        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "<html>Der gewünschte Dispokredit gibt an, wie weit Sie Ihr Konto überziehen dürfen.<br>" +
                                "Ein Bänker muss dies Bestätigen. Bitte geben Sie einen ganzzahligen Wert in Euro an.</html>");
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

        // call onOK() on ENTER
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
        // add your code here
        if (buttonGroupAccountType.getSelection() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sie müssen einen Kontotyp auswählen!",
                    "Kontotyp erforderlich",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (textFieldDispo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sie müssen einen maximalen Dispositionskredit angeben!",
                    "Dispo erforderlich",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        textFieldDispo.setText(textFieldDispo.getText().replace(",", "."));
        try {
            Float.parseFloat(textFieldDispo.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sie müssen einen gültigen Wert als maximalen Dispositionskredit angeben!",
                    "Dispo ungültig",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        float dispo = Float.parseFloat(textFieldDispo.getText());
        dispo = Math.round(dispo * 100f)/100f;
        if (dispo < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sie müssen einen gültigen positiven Wert als maximalen Dispositionskredit angeben!",
                    "Dispo negativ",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        int accountType = -1;
        BankAccount b;
        if (buttonGroupAccountType.isSelected(girokontoRadioButton.getModel())) {
            accountType = 0;
        }else if (buttonGroupAccountType.isSelected(fixedDepositAccountRadioButton.getModel())) {
            accountType = 1;
        }else if (buttonGroupAccountType.isSelected(creditAccountRadioButton.getModel())) {
            accountType = 2;
        }else if (buttonGroupAccountType.isSelected(savingsAccountRadioButton.getModel())) {
            accountType = 3;
        } else if (buttonGroupAccountType.isSelected(sharedAccountRadioButton.getModel())) {
            accountType = 4;
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Nanana! Soooo nicht...",
                    "Schneller als die Polizei erlaubt",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        try {
            UserControl.control.createNewBankAccount(accountType, dispo, textFieldName.getText());
        } catch (DuplicateKeyException e) {
            JOptionPane.showMessageDialog(this, "Der Kontoname ist bereits vergeben! Bitte wählen Sie einen anderen.", "Name bereits vergeben", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
