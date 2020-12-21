package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DataBase {
   final private String url;

    public DataBase(String url) {
        this.url = url;
        createTable();
    }

    private Connection connect() {
        Connection con = null;
        String sql = "jdbc:sqlite:" + url;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(sql);

        try {
            //con = DriverManager.getConnection(url);
            /* Краще конектитись через DataSource*/
            con = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }

    private void createTable() {

        String table = "CREATE TABLE IF NOT EXISTS card(" +
                "id INTEGER PRIMARY KEY, " +
                "number TEXT, " +
                "pin TEXT, " +
                "balance INTEGER DEFAULT 0)";

        try (Statement statement = this.connect().createStatement()) {
            statement.executeUpdate(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String number, int pin) {

        String sql = "INSERT INTO card(number, pin) VALUES(?,?)";

        try (Connection con = this.connect()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, number);
            preparedStatement.setString(2, Integer.toString(pin));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<String> selectNumbers() {

        List<String> numbers = new ArrayList<>();

        String sql = "SELECT number FROM card";

        try (Connection con = this.connect()) {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while(resultSet.next()) {
               numbers.add(resultSet.getString("number"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return numbers;
    }


    public boolean selectLog(String number, int pin) {
        String sql = "SELECT number, pin FROM card";

        try (Connection con = connect()) {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                if (resultSet.getString("number").equals(number)
                && resultSet.getString("pin").equals(Integer.toString(pin))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int selectBalance(String number) {
        String sql = "SELECT balance FROM card WHERE number = ?";

        try (Connection con = connect()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, number);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getInt("balance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public void updateBalance(String number, long income) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection con = connect()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setLong(1, income);
            preparedStatement.setString(2, number);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCard(String number) {
        String sql = "DELETE FROM card WHERE number = ?";

        try (Connection con = connect()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, number);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transfer(String number, String numberForTransfer, int money) {

        String updateNumber = "UPDATE card SET balance = balance - ? WHERE number = ?";

        String updateNumberForTransfer = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection con = connect()) {
            con.setAutoCommit(false);

            try (PreparedStatement psNumber = con.prepareStatement(updateNumber);
                 PreparedStatement psNumberForTransfer = con.prepareStatement(updateNumberForTransfer)) {

                //update the main card (transfer money from)
                psNumber.setInt(1, money);
                psNumber.setString(2, number);
                psNumber.executeUpdate();

                //update the second card(transfer money to)
                psNumberForTransfer.setInt(1, money);
                psNumberForTransfer.setString(2, numberForTransfer);
                psNumberForTransfer.executeUpdate();

                con.commit();

            } catch (SQLException e) {
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}