package UI;

import Data.BankAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProfileSettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldForename;
    private JTextField textFieldLastname;
    private JComboBox comboBox1;

    public ProfileSettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setIconImage(new ImageIcon("src/assets/sign.png").getImage());
        setTitle("Profileinstellungen");

        textFieldForename.setText(UserControl.control.getUser().forename);
        textFieldLastname.setText(UserControl.control.getUser().lastname);

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

        for (BankAccount b : UserControl.control.getBankAccounts()) {
            comboBox1.addItem(b);
            if (b.name.equals(UserControl.control.getUser().mainAccountName)) {
                comboBox1.setSelectedItem(b);
            }
        }

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        UserControl.control.updateNewMainAccount((BankAccount) comboBox1.getSelectedItem());
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
