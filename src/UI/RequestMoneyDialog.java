package UI;

import Data.BankAccount;
import Data.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RequestMoneyDialog extends JDialog implements ISelectReceiver{
    // TODO: Zu Personen Klasse ändern
    public String[] persons = new String[] {};
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList listFrom;
    private JComboBox comboBoxAccount;
    private JTextField textFieldTotal;
    private JTextArea textAreaPurpose;
    private JButton btnAdd;
    private JButton btnDelete;

    public RequestMoneyDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Geld anfordern");
        setIconImage(new ImageIcon("src/assets/handout.png").getImage());


        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectContacts();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel model = (DefaultListModel) listFrom.getModel();
                int selectedIndex = listFrom.getSelectedIndex();
                if (selectedIndex != -1) {
                    model.remove(selectedIndex);
                }
            }
        });

        for (BankAccount b : UserControl.control.getBankAccounts()) {
            comboBoxAccount.addItem(b);
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
        textFieldTotal.setText(textFieldTotal.getText().replace(",", "."));
        float total = -1f;
        try {
            total = Float.parseFloat(textFieldTotal.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Der Betrag hat kein gültiges Format! Bitte geben Sie einen Zahlenwert mit maximal zwei Nachkommastellen ein.", "Betrag falsch", JOptionPane.ERROR_MESSAGE);
            return;
        }
        total = Math.round(total * 100f) / 100f;
        if (total <= 0) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie einen positiven Betrag mit maximal zwei Nachkommastellen an.", "Betrag zu klein", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(listFrom.getModel().getSize() <= 0) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie mindestens einen Kontakt aus!", "Kontakt auswählen", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Möchten Sie " + total + "€ anfordern?");

        if (choice == JOptionPane.OK_OPTION) {
            // TODO: UserControl ausführen
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        RequestMoneyDialog dialog = new RequestMoneyDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    @Override
    public void receiveSelectedContacts(Person[] contacts) {
        DefaultListModel model = (DefaultListModel) listFrom.getModel();
        model.clear();
        for(Person p : contacts) {
            model.addElement(p);
        }
    }

    private void selectContacts() {
        new SelectContacts(this);
    }
}
