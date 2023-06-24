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

public class CreateUserDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtForename;
    private JTextField txtLastname;
    private JFormattedTextField fTxtDate;
    private JTextField txtMail;
    private JTextField txtStreet;
    private JTextField txtNo;
    private JTextField txtPostal;
    private JTextField txtPhone;
    private Date parsedDate = null;

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

        setDateFormat();

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
    }

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
        if (!isValidEmail()) {
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

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void setDateFormat() {
        MaskFormatter maskFormatter;
        try {
            maskFormatter = new MaskFormatter("##.##.####");
            maskFormatter.setPlaceholderCharacter('_');
            maskFormatter.setValidCharacters("0123456789.");

            fTxtDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(maskFormatter));

            fTxtDate.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    String input = fTxtDate.getText();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    dateFormat.setLenient(false);

                    try {
                        parsedDate = dateFormat.parse(input);

                        Calendar minDate = Calendar.getInstance();
                        minDate.add(Calendar.YEAR, -150);

                        Calendar maxDate = Calendar.getInstance();
                        maxDate.add(Calendar.DAY_OF_MONTH, 1); // Exclude the current day

                        Calendar inputDate = Calendar.getInstance();
                        inputDate.setTime(parsedDate);

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


    private boolean isValidEmail() {
        String email = txtMail.getText();
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);

        return pattern.matcher(email).matches();
    }


}
