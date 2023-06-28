package UI;

import javax.swing.*;
import java.awt.event.*;

/**
 * Displays a dialog to log in the user with forename, lastname and password.
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class LoginDialog extends JDialog {
    /**
     * the panel containing the Login Dialog
     */
    private JPanel contentPane;
    /**
     * the OK-Button
     */
    private JButton buttonOK;
    /**
     * The Cancel-Button
     */
    private JButton buttonCancel;
    /**
     * the textfield used for the password
     */
    private JPasswordField password;
    /**
     * the textfield used for the forename
     */
    private JTextField txtForename;
    /**
     * the textfield used for the Lastname
     */
    private JTextField txtLastname;

    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Anmelden");
        Program.setIcon(this, "business-contact-male");
        setResizable(false);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { onCancel(); }
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
        setVisible(true);
    }

    /**
     * trys to log in the user account that matches the inputs
     */
    private void onOK() {
        try {
            UserControl.control.login(txtForename.getText(), txtLastname.getText(), password.getPassword());
            dispose();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Die Anmeldedaten sind falsch! Bitte erneut versuchen", "Anmeldung fehlgeschlagen", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * closes the window
     */
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
