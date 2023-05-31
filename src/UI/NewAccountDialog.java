package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NewAccountDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton girokontoRadioButton;
    private JRadioButton festgeldkontoRadioButton;
    private JRadioButton kreditkarteRadioButton;
    private JRadioButton depotRadioButton;
    private JTextField textFieldDispo;
    private JButton infoButton;
    private JTextField textFieldName;
    private ButtonGroup buttonGroupType;

    public NewAccountDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(200, -1);
        setTitle("Konto erstellen");
        setIconImage(new ImageIcon("src/assets/money-bag.png").getImage());



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
        if (buttonGroupType.getSelection() == null) {
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

        try {
            Integer.parseInt(textFieldDispo.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sie müssen einen gültigen ganzzahligen Wert als maximalen Dispositionskredit angeben!",
                    "Dispo ungültig",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        int accountType = -1;
        if (buttonGroupType.isSelected(girokontoRadioButton.getModel())) {
            accountType = 0;
        }else if (buttonGroupType.isSelected(festgeldkontoRadioButton.getModel())) {
            accountType = 1;
        }else if (buttonGroupType.isSelected(kreditkarteRadioButton.getModel())) {
            accountType = 2;
        }else if (buttonGroupType.isSelected(depotRadioButton.getModel())) {
            accountType = 3;
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Nanana! Soooo nicht...",
                    "Schneller als die Polizei erlaubt",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        UserControl.control.createNewBankAccount(accountType, Integer.parseInt(textFieldDispo.getText()), textFieldName.getText());
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        NewAccountDialog dialog = new NewAccountDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
