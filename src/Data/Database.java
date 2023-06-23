package Data;
import CustomExceptions.DuplicateKeyException;
import UI.UserControl;

import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        try {
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
                    "SELECT 'password', role, main_account, is_admin FROM user WHERE forename = ? AND lastname = ?"
            );
            statement.setString(1, forename);
            statement.setString(2, lastname);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (new String(password).equals(result.getString("password"))) {
                    Person p = new Person(forename, lastname, result.getString("role"));
                    p.mainAccountName = result.getString("main_account");
                    p.isAdmin = result.getBoolean("is_admin");
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

    public Person[] getPendingRequests(Person p) {
        ArrayList<Person> list = new ArrayList<Person>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM contacts WHERE secound_lastname = ? AND second_forename = ? AND pending = 1"
            );
            statement.setString(1, p.lastname);
            statement.setString(2, p.forename);
            ResultSet result = statement.executeQuery();
            handleContactsResponse(p, list, result);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new Person[0]);
    }

    private void handleContactsResponse(Person p, ArrayList<Person> list, ResultSet result) throws SQLException {
        while (result.next()) {
            String forename = "";
            if (p.forename.equals(result.getString("first_forename"))) {
                forename = result.getString("second_forename");
            } else {
                forename = result.getString("first_forename");
            }

            String lastname = "";
            if (p.lastname.equals(result.getString("first_lastname"))) {
                lastname = result.getString("secound_lastname");
            } else {
                lastname = result.getString("first_lastname");
            }

            Person person = new Person(forename, lastname);
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM user WHERE forename = ? AND lastname = ?"
            );
            statement.setString(1, forename);
            statement.setString(2, lastname);
            ResultSet result2 = statement.executeQuery();
            while (result2.next()) {
                person.mainAccountName = result2.getString("main_account");
                person.role = result2.getString("role");
            }

            list.add(person);
        }
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
            handleContactsResponse(p, list, result);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new Person[0]);
    }

    public BankAccount[] getBankAccounts(Person p) {
        ArrayList<BankAccount> list = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM accounts WHERE ((forename = ? AND lastname = ?) OR " +
                            "(secound_forename = ? AND secound_lastname = ?)) AND inactive = false AND pending = false"
            );
            statement.setString(1, p.forename);
            statement.setString(2, p.lastname);
            statement.setString(3, p.forename);
            statement.setString(4, p.lastname);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                bankAccountFromResponse(list, result, p);
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
                    "SELECT * FROM\n" +
                            "    transactions as t\n" +
                            "LEFT JOIN accounts as a ON\n" +
                            "    a.name = t.from_account\n" +
                            " WHERE\n" +
                            "     t.from_account = ? OR t.to_account = ?\n" +
                            "ORDER BY time DESC;"
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
                if (from.name != null && from.name.equals(b.name)) {
                    from = b;
                    total = result.getFloat("total") * -1;
                    PreparedStatement statement2 = connection.prepareStatement(
                            "SELECT * FROM accounts WHERE name = ?;"
                    );
                    statement2.setString(1, to.name);
                    ResultSet result2 = statement2.executeQuery();
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
                list.add(new Transaction(from, to, total, result.getString("purpose"), result.getTimestamp("time")));
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
                    statement.setFloat(i + 1, Float.valueOf(values[i]));
                } else {
                    statement.setString(i + 1, values[i]);
                }
            }
            int rowsInserted = 0;
            try {
                rowsInserted = statement.executeUpdate();
            } catch (SQLException e) {
                throw new DuplicateKeyException();
            }
            if (rowsInserted != 1) {
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
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UserControl.control.reloadUser();
    }

    public void deleteFriendship(Person me, Person other) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM contacts WHERE " +
                            "((first_forename = ? AND first_lastname = ?) OR" +
                            "(second_forename = ? AND secound_lastname = ?)) AND" +
                            "((first_forename = ? AND first_lastname = ?) OR" +
                            "(second_forename = ? AND secound_lastname = ?))"
            );
            statement.setString(1, me.forename);
            statement.setString(2, me.lastname);
            statement.setString(3, me.forename);
            statement.setString(4, me.lastname);
            statement.setString(5, other.forename);
            statement.setString(6, other.lastname);
            statement.setString(7, other.forename);
            statement.setString(8, other.lastname);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPendingFriendship(Person me, Person other) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO java_banking.contacts (first_forename, first_lastname, second_forename, secound_lastname)" +
                            "VALUES (?,?,?,?)"
            );
            statement.setString(1, me.forename);
            statement.setString(2, me.lastname);
            statement.setString(3, other.forename);
            statement.setString(4, other.lastname);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void acceptPendingFriendship(Person me, Person other) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE contacts " +
                            "SET pending = false WHERE " +
                            "((first_forename = ? AND first_lastname = ?) OR" +
                            "(second_forename = ? AND secound_lastname = ?)) AND" +
                            "((first_forename = ? AND first_lastname = ?) OR" +
                            "(second_forename = ? AND secound_lastname = ?))"
            );
            statement.setString(1, me.forename);
            statement.setString(2, me.lastname);
            statement.setString(3, me.forename);
            statement.setString(4, me.lastname);
            statement.setString(5, other.forename);
            statement.setString(6, other.lastname);
            statement.setString(7, other.forename);
            statement.setString(8, other.lastname);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(BankAccount b) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE accounts " +
                            "SET inactive = true " +
                            "WHERE " +
                            "((forename = ? AND lastname = ?) OR (secound_forename = ? AND secound_lastname = ?)) AND name = ?"
            );
            statement.setString(1, b.owner.forename);
            statement.setString(2, b.owner.lastname);
            statement.setString(3, b.owner.forename);
            statement.setString(4, b.owner.lastname);
            statement.setString(5, b.name);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void moneyTransaction(BankAccount f, Person t, float total, String purpose) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO transactions (from_account, to_account, total, purpose) " +
                            "VALUES (?,?,?,?)"
            );
            statement.setString(1, f.name);
            statement.setString(2, t.mainAccountName);
            statement.setFloat(3, total);
            statement.setString(4, purpose);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void moveMoney(BankAccount f, BankAccount t, float v, String p) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO transactions (from_account, to_account, total, purpose) " +
                            "VALUES (?,?,?,?)"
            );
            statement.setString(1, f.name);
            statement.setString(2, t.name);
            statement.setFloat(3, v);
            statement.setString(4, p);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PayRequest[] getPayRequests (Person p) {
        ArrayList<PayRequest> list = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM pay as p, accounts as a WHERE p.from = a.name AND p.to_forename = ? AND p.to_lastname = ?"
            );
            statement.setString(1, p.forename);
            statement.setString(2, p.lastname);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                PayRequest pay = new PayRequest();
                pay.id = result.getInt("ID");
                pay.from = new BankAccount();
                pay.from.name = result.getString("from");
                pay.from.owner = new Person(
                        result.getString("forename"),
                        result.getString("lastname")
                );
                pay.total = result.getFloat("total");
                pay.purpose = result.getString("purpose");
                list.add(pay);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new PayRequest[]{});
    }

    public void pay(PayRequest p, BankAccount from) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO transactions (from_account, to_account, total, purpose) " +
                            "VALUES (?,?,?,?)"
            );
            statement.setString(1, from.name);
            statement.setString(2, p.from.name);
            statement.setFloat(3, p.total);
            statement.setString(4, p.purpose);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePayRequest(PayRequest p) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE " +
                            "FROM pay " +
                            "WHERE ID = ?;"
            );
            statement.setInt(1, p.id);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PayRequest[] createPayRequests(PayRequest[] p) {
        for (PayRequest pay : p) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO java_banking.pay (`from`, to_forename, to_lastname, total, purpose) " +
                                "VALUES (?,?,?,?,?);",
                        Statement.RETURN_GENERATED_KEYS
                );
                statement.setString(1, pay.from.name);
                statement.setString(2, pay.to.forename);
                statement.setString(3, pay.to.lastname);
                statement.setFloat(4, pay.total);
                statement.setString(5, pay.purpose);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted != 1) {
                    System.out.println(rowsInserted);
                }
                ResultSet r = statement.getGeneratedKeys();
                while (r.next()) {
                    pay.id = r.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    public Person[] getRelatedUsers(Person p) {
        ArrayList<Person> pers = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM " +
                            "admin_rel as a, user as u " +
                            "WHERE " +
                            "a.customer_forename=u.forename AND " +
                            "a.customer_lastname = u.lastname AND " +
                            "a.admin_forename = ? AND " +
                            "a.admin_lastname = ?;"
            );
            statement.setString(1, p.forename);
            statement.setString(2, p.lastname);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Person tmp = new Person(
                        result.getString("forename"),
                        result.getString("lastname"),
                        result.getString("role")
                );
                tmp.mainAccountName = result.getString("main_account");
                pers.add(tmp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pers.toArray(new Person[]{});
    }

    public float getBankAccountValue(BankAccount b) {
        float minus = 0f;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(total) as minus FROM " +
                            "transactions " +
                            "WHERE " +
                            "from_account = ?;"
            );
            statement.setString(1, b.name);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                minus = result.getFloat("minus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        float plus = 0f;
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(total) as plus FROM " +
                            "transactions " +
                            "WHERE " +
                            "to_account = ?;"
            );
            statement.setString(1, b.name);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                plus = result.getFloat("plus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plus - minus;
    }

    public void changeUserForename(String old, Person user) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE user " +
                            "SET forename = ? " +
                            "WHERE " +
                            "forename = ? AND lastname = ?;"
            );
            statement.setString(1, user.forename);
            statement.setString(2, old);
            statement.setString(3, user.lastname);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeUserLastname(String old, Person user) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE user " +
                            "SET lastname = ? " +
                            "WHERE " +
                            "forename = ? AND lastname = ?;"
            );
            statement.setString(1, user.lastname);
            statement.setString(2, user.forename);
            statement.setString(3, old);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeUser(Person user) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE user " +
                            "SET role = ?, is_admin = ?" +
                            "WHERE " +
                            "forename = ? AND lastname = ?;"
            );
            statement.setString(1, user.role);
            statement.setBoolean(2, user.isAdmin);
            statement.setString(3, user.forename);
            statement.setString(4, user.lastname);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted != 1) {
                System.out.println(rowsInserted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BankAccount[] getPendingAccounts(Person u) {
        ArrayList<BankAccount> list = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM accounts\n" +
                            "WHERE (forename = ? OR secound_forename = ?)\n" +
                            "    AND (lastname = ? OR secound_lastname = ?)\n" +
                            "    AND pending = true\n" +
                            "UNION\n" +
                            "SELECT c.* FROM accounts AS c\n" +
                            "INNER JOIN admin_rel AS a\n" +
                            "    ON (a.customer_forename = c.forename OR a.customer_forename = c.secound_forename)\n" +
                            "    AND (a.customer_lastname = c.lastname OR a.customer_lastname = c.secound_lastname)\n" +
                            "WHERE a.admin_forename = ? AND a.admin_lastname = ? AND c.pending = true;\n"
            );
            statement.setString(1, u.forename);
            statement.setString(2, u.forename);
            statement.setString(3, u.lastname);
            statement.setString(4, u.lastname);
            statement.setString(5, u.forename);
            statement.setString(6, u.lastname);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Person p = new Person(result.getString("forename"), result.getString("lastname"));
                bankAccountFromResponse(list, result, p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new BankAccount[]{});
    }

    private void bankAccountFromResponse(ArrayList<BankAccount> list, ResultSet result, Person p) throws SQLException {
        switch (result.getString("type")) {
            case "normal":
                BankAccount b = new BankAccount(p, result.getString("name"));
                b.setOverdraftFacility(result.getFloat("overdraftFacility"));
                list.add(b);
                break;
            case "fixed_deposit":
                FixedDepositAccount f = new FixedDepositAccount();
                f.owner = p;
                f.name = result.getString("name");
                f.setOverdraftFacility(result.getFloat("overdraftFacility"));
                f.accessDate = result.getDate("accessDate");
                list.add(f);
                break;
            case "saving":
                SavingAccount s = new SavingAccount();
                s.owner = p;
                s.name = result.getString("name");
                s.setOverdraftFacility(result.getFloat("overdraftFacility"));
                s.reference = new BankAccount();
                s.reference.name = result.getString("reference");
                list.add(s);
                break;
            case "shared":
                SharedAccount sh = new SharedAccount();
                sh.owner = p;
                sh.name = result.getString("name");
                sh.setOverdraftFacility(result.getFloat("overdraftFacility"));
                sh.secondOwner = new Person(result.getString("secound_forename"), result.getString("secound_lastname"));
                list.add(sh);
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

    public Person[] getNotCaredCustomers() {
        ArrayList<Person> list = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * " +
                            "FROM user " +
                            "WHERE (forename, lastname) NOT IN ( " +
                            "    SELECT customer_forename, customer_lastname FROM admin_rel " +
                            "    UNION " +
                            "    SELECT admin_forename, admin_lastname FROM admin_rel " +
                            ") AND role != 'Bänker';"
            );
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Person p = new Person(
                        result.getString("forename"),
                        result.getString("lastname"),
                        result.getString("role")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new Person[]{});
    }

    public void careForCustomer(Person m, Person c) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO admin_rel (admin_forename, admin_lastname, customer_forename, customer_lastname) " +
                            "VALUES (?,?,?,?);"
            );
            statement.setString(1, m.forename);
            statement.setString(2, m.lastname);
            statement.setString(3, c.forename);
            statement.setString(4, c.lastname);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}