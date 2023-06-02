package Data;
import CustomExceptions.DuplicateKeyException;
import UI.UserControl;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Database {

    private Connection connection;

    public void connect() {
        System.out.println("Lade Fahrer...");

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            System.out.println("Konnte den Treiber nicht finden. Noooooob!");
            System.exit(100);
        }

        // jdbc:mariadb://hostname:port/databasename
        String url = "jdbc:mariadb://h2953193.stratoserver.net:3306/java_banking";
        String username = "java_sys";
        String password = "java_system_user";

        System.out.println("Verbinde Datenbank...");

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");
            this.connection = connection;
        } catch (SQLException e) {
            System.out.println("Konnte keine Verbindung zur Datenbank herstellen. Denk noch mal drüber nach!");
            System.exit(101);
        }

    }

    public void finish() {
        try {
            connection.close();
        } catch (SQLException e) {
        }
    }

    public Person checkPerson(String forename, String lastname, char[] password) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT 'password', role, main_account FROM user WHERE forename = ? AND lastname = ?"
            );
            statement.setString(1, forename);
            statement.setString(2, lastname);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (new String(password).equals(result.getString("password"))) {
                    Person p =  new Person(forename, lastname, result.getString("role"));
                    p.mainAccountName = result.getString("main_account");
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Person[] getAvailableFriends(Person p) {
        ArrayList<Person> list = new ArrayList<Person>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM user WHERE lastname != ? AND forename != ?"
            );
            statement.setString(1, p.lastname);
            statement.setString(2, p.forename);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                list.add(new Person(
                        result.getString("forename"),
                        result.getString("lastname"),
                        result.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM contacts WHERE (first_lastname = ? AND first_forename = ?) OR (secound_lastname = ? AND second_forename = ?)"
            );
            statement.setString(1, p.lastname);
            statement.setString(2, p.forename);
            statement.setString(3, p.lastname);
            statement.setString(4, p.forename);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                for (int i = 0; i < list.size(); i++) {
                    Person l = list.get(i);
                    if (
                            l.forename.equals(result.getString("first_forename")) ||
                            l.forename.equals(result.getString("second_forename")) ||
                            l.lastname.equals(result.getString("first_lastname")) ||
                            l.lastname.equals(result.getString("secound_lastname"))
                    ) {
                        list.remove(i);
                        i--;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.toArray(new Person[0]);
    }

    public Person[] getPendigRequests(Person p) {
        ArrayList<Person> list = new ArrayList<Person>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM contacts WHERE ((first_lastname = ? AND first_forename = ?) OR (secound_lastname = ? AND second_forename = ?)) AND pending = 1"
            );
            statement.setString(1, p.lastname);
            statement.setString(2, p.forename);
            statement.setString(3, p.lastname);
            statement.setString(4, p.forename);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String forename = "";
                if(p.forename.equals(result.getString("first_forename"))) {
                    forename = result.getString("second_forename");
                } else {
                    forename = result.getString("first_forename");
                }

                String lastname = "";
                if(p.lastname.equals(result.getString("first_lastname"))) {
                    lastname = result.getString("secound_lastname");
                } else {
                    lastname = result.getString("first_lastname");
                }
                list.add(new Person(forename, lastname));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new Person[0]);
    }

    public Person[] getContacts(Person p) {
        ArrayList<Person> list = new ArrayList<Person>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM contacts WHERE ((first_lastname = ? AND first_forename = ?) OR (secound_lastname = ? AND second_forename = ?)) AND pending = 0"
            );
            statement.setString(1, p.lastname);
            statement.setString(2, p.forename);
            statement.setString(3, p.lastname);
            statement.setString(4, p.forename);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String forename = "";
                if(p.forename.equals(result.getString("first_forename"))) {
                    forename = result.getString("second_forename");
                } else {
                    forename = result.getString("first_forename");
                }

                String lastname = "";
                if(p.lastname.equals(result.getString("first_lastname"))) {
                    lastname = result.getString("secound_lastname");
                } else {
                    lastname = result.getString("first_lastname");
                }
                list.add(new Person(forename, lastname));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new Person[0]);
    }

    public BankAccount[] getBankAccounts(Person p) {
        ArrayList<BankAccount> list = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM accounts WHERE forename = ? AND lastname = ?"
            );
            statement.setString(1, p.forename);
            statement.setString(2, p.lastname);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                switch (result.getString("type")) {
                    // TODO: Kontotypen initialisieren
                    case "normal":
                        BankAccount b = new BankAccount(p,result.getString("name"));
                        b.setOverdraftFacility(result.getFloat("overdraftFacility"));
                        list.add(b);
                        break;
                    case "fixed_deposit":
                        break;
                    case "saving":
                        break;
                    case "shared":
                        break;
                    case "credit":
                        CreditAccount c = new CreditAccount();
                        c.name = result.getString("name");
                        c.referenceAccount = new BankAccount();
                        c.referenceAccount.name = result.getString("reference");
                        c.owner = p;
                        c.setOverdraftFacility(result.getFloat("overdraftFacility"));
                        list.add(c);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new BankAccount[0]);
    }

    public Transaction[] getTransactions(BankAccount b) {
        ArrayList<Transaction> list = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM transactions as t, accounts as a" +
                            "         WHERE" +
                            "             a.name = t.from_account AND" +
                            "             (t.from_account = ? OR t.to_account = ?);"
            );
            statement.setString(1, b.name);
            statement.setString(2, b.name);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                float total = 0.0f;
                BankAccount from = new BankAccount();
                from.name = result.getString("from_account");
                BankAccount to = new BankAccount();
                to.name = result.getString("to_account");
                if (from.name.equals(b.name)) {
                    from = b;
                    total = result.getFloat("total") * -1;
                    PreparedStatement statement2 = connection.prepareStatement(
                            "SELECT * FROM accounts WHERE name = ?;"
                    );
                    statement2.setString(1, to.name);
                    ResultSet result2 = statement.executeQuery();
                    while (result2.next()) {
                        to.owner = new Person(
                                result2.getString("forename"),
                                result2.getString("lastname")
                        );
                    }
                } else {
                    total = result.getFloat("total");
                    to = b;
                    from.owner = new Person(
                            result.getString("forename"),
                            result.getString("lastname")
                    );
                }
                list.add(new Transaction(from, to, total, result.getString("purpose"), result.getDate("time")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new Transaction[0]);
    }

    public void createNewBankAccount(BankAccount b) throws DuplicateKeyException {
        String[] values = new String[9];
        values[0] = b.name;
        values[1] = b.owner.forename;
        values[2] = b.owner.lastname;
        values[3] = String.valueOf(b.getOverdraftFacility());
        values[8] = "normal";
        if (b instanceof CreditAccount) {
            values[7] = ((CreditAccount) b).referenceAccount.name;
            values[8] = "credit";
        } else if (b instanceof FixedDepositAccount) {

        } else if (b instanceof SavingAccount) {
            values[3] = "0";
            values[7] = ((SavingAccount) b).reference.name;
            values[8] = "saving";
        } else if (b instanceof SharedAccount) {
            values[5] = ((SharedAccount) b).secondOwner.forename;
            values[6] = ((SharedAccount) b).secondOwner.lastname;
            values[8] = "shared";
        }
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO java_banking.accounts (`name`, forename, lastname, overdraftFacility, accessDate, secound_forename," +
                            "                                   secound_lastname, reference, `type`)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            for (int i = 0; i < values.length; i++) {
                if (i == 3) {
                    statement.setFloat(i+1, Float.valueOf(values[i]));
                } else {
                    statement.setString(i+1, values[i]);
                }
            }
            int rowsInserted = 0;
            try {
                rowsInserted = statement.executeUpdate();
            } catch (SQLException e) {
                throw new DuplicateKeyException();
            }
            if(rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UserControl.control.ui.refreshBankAccounts();
    }

    public void updateNewMainAccount(Person p, BankAccount b) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE user t SET t.main_account = ? WHERE t.forename= ? AND t.lastname = ?;"
            );
            statement.setString(1, b.name);
            statement.setString(2, p.forename);
            statement.setString(3, p.lastname);
            int rowsInserted = statement.executeUpdate();
            if(rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UserControl.control.reloadUser();
    }
}
