package UI;

import Data.BankAccount;
import Data.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileSettingsDialog extends JDialog implements IParsedBirthdayReceiver {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtForename;
    private JTextField txtLastname;
    private JComboBox comboBox1;
    private JFormattedTextField fTxtDate;
    private JTextField txtMail;
    private JTextField txtStreet;
    private JTextField txtNo;
    private JTextField txtPLZ;
    private JTextField txtPhone;

    private Date parsedDate = null;


    public ProfileSettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        Program.setIcon(this, "sign");
        setTitle("Profileinstellungen");

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

        CreateUserDialog.setDateFormat(fTxtDate, this);
        setTxt(UserControl.control.getUser());

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
        if(fTxtDate.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie ein Geburtsdatum ein.", "Geburtsdatum fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(txtMail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie eine Mail ein.", "Mail fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!CreateUserDialog.isValidEmail(txtMail)) {
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
        if(txtPLZ.getText().isEmpty()) {
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
        u.postal = txtPLZ.getText();
        u.phone = txtPhone.getText();


        UserControl.control.updateNewMainAccount((BankAccount) comboBox1.getSelectedItem());
        UserControl.control.updateUser(u);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    @Override
    public void setParsedDate(Date d) {
        parsedDate = d;
    }

    @Override
    public Date getParsedDate() {
        return parsedDate;
    }

    private void setTxt(Person p) {
        txtForename.setText(p.forename);
        txtLastname.setText(p.lastname);

        if (p.date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String formattedDate = dateFormat.format(p.date);
            fTxtDate.setText(formattedDate);
            parsedDate = p.date;
        }
        txtMail.setText(p.mail);
        txtStreet.setText(p.street);
        txtNo.setText(p.no);
        txtPLZ.setText(p.postal);
        txtPhone.setText(p.phone);
    }
}
