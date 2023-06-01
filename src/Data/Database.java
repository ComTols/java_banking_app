package Data;
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
                "SELECT 'password', role FROM user WHERE forename = ? AND lastname = ?"
            );
            statement.setString(1, forename);
            statement.setString(2, lastname);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (new String(password).equals(result.getString("password"))) {
                    return new Person(forename, lastname, result.getString("role"));
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
            statement.setString(1, b.name);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                BankAccount from = new BankAccount();
                from.name = result.getString("from_account");
                BankAccount to = new BankAccount();
                to.name = result.getString("to_account");
                if (from.name.equals(b.name)) {
                    from = b;
                    //TODO: Wenn to ist anderes Konto, dann neue Abfrage über to.name in accounts um user zu finden
                } else {
                    to = b;
                    from.owner = new Person(
                            result.getString("forename"),
                            result.getString("lastname")
                    );
                }
                list.add(new Transaction(from, to, result.getFloat("total"), result.getString("purpose"), result.getDate("time")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new Transaction[0]);
    }
}
