package UI;

import Data.Person;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Shows a dialog box to create a new user.
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class CreateUserDialog extends JDialog implements IParsedBirthdayReceiver {
    /**
     * panel containing the User Dialog
     */
    private JPanel contentPane;
    /**
     * the OK-Button
     */
    private JButton buttonOK;
    /**
     * The cancel-Button
     */
    private JButton buttonCancel;
    /**
     * the textfield used for the forename
     */
    private JTextField txtForename;
    /**
     * the textfield used for the lastname
     */
    private JTextField txtLastname;
    /**
     * the textfield used for the date
     */
    private JFormattedTextField fTxtDate;
    /**
     * textfield used for the email
     */
    private JTextField txtMail;
    /**
     * the textfield used for the street
     */
    private JTextField txtStreet;
    /**
     * the textfield used for the housenumber
     */
    private JTextField txtNo;
    /**
     * the textfield used for the Postal
     */
    private JTextField txtPostal;
    /**
     * the textfield used for the Phone-number
     */
    private JTextField txtPhone;
    private Date parsedDate = null;

    /**
     * Creates a User-Dialog
     */
    public CreateUserDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Neuer Benutzer");
        Program.setIcon(this, "hat");

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setDateFormat(fTxtDate, this);

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
    }

    /**
     * triggers on click and validates the User-Input
     */
    private void onOK() {
        if(txtForename.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie einen Vornamen ein.", "Vorname fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(txtLastname.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie einen Nachnamen ein.", "Nachname fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(fTxtDate.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie ein Geburtsdatum ein.", "Geburtsdatum fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(txtMail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie eine Mail ein.", "Mail fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!isValidEmail(txtMail)) {
            JOptionPane.showMessageDialog(this, "Die Mail Adresse ist fehlerhaft.", "Mail falsch", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(txtStreet.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie eine Straße ein.", "Straße fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(txtNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie eine Hausnummer ein.", "Hausnummer fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(txtPostal.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie eine PLZ ein.", "PLZ fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(txtPhone.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie eine Telefonnummer ein.", "Telefonnummer fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Person u = new Person(
                txtForename.getText(),
                txtLastname.getText()
        );
        u.date = parsedDate;
        u.mail = txtMail.getText();
        u.street = txtStreet.getText();
        u.no = txtNo.getText();
        u.postal = txtPostal.getText();
        u.phone = txtPhone.getText();

        String pw = UserControl.control.createUser(u);
        JTextField txtPW = new JTextField();
        txtPW.setEditable(false);
        txtPW.setText("Der Benutzer wurde erfolgreich angelegt. Das Passwort lautet: " + pw);
        JOptionPane.showMessageDialog(this, txtPW, "Erfolgreich angelegt", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    /**
     * triggers when cancel button is clicked, closes the window
     */
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    /**
     * masks the given date in the required format
     * @param fTxtDate the given date
     * @param receiver reference to the receiving instance
     */
    public static void setDateFormat(JFormattedTextField fTxtDate, IParsedBirthdayReceiver receiver) {
        MaskFormatter maskFormatter;
        try {
            maskFormatter = new MaskFormatter("##.##.####");
            maskFormatter.setPlaceholderCharacter('_');
            maskFormatter.setValidCharacters("0123456789.");

            fTxtDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(maskFormatter));

            fTxtDate.addFocusListener(new java.awt.event.FocusAdapter() {
                /**
                 * validates the date, it must not be in the future or more than 150 years in the past
                 * @param evt the focus event that triggers the validation
                 */
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    String input = fTxtDate.getText();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    dateFormat.setLenient(false);

                    try {
                        receiver.setParsedDate(dateFormat.parse(input));

                        Calendar minDate = Calendar.getInstance();
                        minDate.add(Calendar.YEAR, -150);

                        Calendar maxDate = Calendar.getInstance();
                        maxDate.add(Calendar.DAY_OF_MONTH, 1); // Exclude the current day

                        Calendar inputDate = Calendar.getInstance();
                        inputDate.setTime(receiver.getParsedDate());

                        if (inputDate.before(minDate) || inputDate.after(maxDate)) {
                            JOptionPane.showMessageDialog(UserControl.control.ui, "Das Datum darf nicht in der Zukunft liegen oder mehr als 150 Jahre in der Vergangenheit", "Datum invalide", JOptionPane.ERROR_MESSAGE);
                            fTxtDate.setText(""); // Clear the field if the date is out of bounds
                        }
                    } catch (ParseException e) {
                        JOptionPane.showMessageDialog(UserControl.control.ui, "Das Datum muss dem Format DD.MM.YYYY entsprechen!", "Datumsformat falsch", JOptionPane.ERROR_MESSAGE);
                        fTxtDate.setText(""); // Clear the field if the input is not a valid date
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks if given email is a valid email
     * @param txtMail the given email
     * @return true if email is valid
     */
    public static boolean isValidEmail(JTextField txtMail) {
        String email = txtMail.getText();
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);

        return pattern.matcher(email).matches();
    }

    /**
     * sets the parsed ID
     * @param d the parsed date
     */
    @Override
    public void setParsedDate(Date d) {
        parsedDate = d;
    }

    /**
     * gets the parsed date
     * @return parsed date
     */
    @Override
    public Date getParsedDate() {
        return parsedDate;
    }
}
