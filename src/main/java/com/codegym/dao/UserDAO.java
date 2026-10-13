package com.codegym.dao;

import com.codegym.model.User;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private final String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=UTC";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "[PASSWORD]";

    private static final String INSERT_USERS_SQL = "INSERT INTO users (name, email, country) VALUES (?, ?, ?);";
    private static final String SELECT_USER_BY_ID = "SELECT id, name, email, country FROM users WHERE id = ?;";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users;";
    private static final String DELETE_USERS_SQL = "DELETE FROM users WHERE id = ?;";
    private static final String UPDATE_USERS_SQL = "UPDATE users SET name = ?, email = ?, country = ? WHERE id = ?;";
    private static final String SEARCH_BY_COUNTRY_SQL = "SELECT * FROM users WHERE country LIKE ?;";
    private static final String SORT_BY_NAME_SQL = "SELECT * FROM users ORDER BY name ASC;";
    private static final String SQL_INSERT =
            "INSERT INTO Employee (name, salary, created_Date) VALUES (?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE Employee SET salary=? WHERE name=?";
    private static final String SQL_TABLE_CREATE =
            "CREATE TABLE Employee"
            + "("
            + " id INT(11) AUTO_INCREMENT,"
            + " name VARCHAR(100) NOT NULL,"
            + " salary DECIMAL(15, 2) NOT NULL,"
            + " created_Date TIMESTAMP,"
            + " PRIMARY KEY (id)"
            + ")";
    private static final String SQL_TABLE_DROP = "DROP TABLE IF EXISTS Employee";

    public UserDAO() {
    }

    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public void insertUser(User user) throws SQLException {
        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getCountry());
            preparedStatement.executeUpdate();
        }
    }


    @Override
    public User selectUser(int id) {
        User user = null;

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                user = new User(id, name, email, country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "{CALL get_all_users()}";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(query);
             ResultSet rs = callableStatement.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("country")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        String query = "{CALL delete_user(?)}";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(query)) {
            callableStatement.setInt(1, id);
            return callableStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        String query = "{CALL update_user(?, ?, ?, ?)}";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(query)) {
            callableStatement.setInt(1, user.getId());
            callableStatement.setString(2, user.getName());
            callableStatement.setString(3, user.getEmail());
            callableStatement.setString(4, user.getCountry());
            return callableStatement.executeUpdate() > 0;
        }
    }

    @Override
    public List<User> searchByCountry(String country) {
        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(SEARCH_BY_COUNTRY_SQL)) {
            statement.setString(1, "%" + country + "%");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("country")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public List<User> sortByName() {
        return getUsers(SORT_BY_NAME_SQL);
    }

    @Override
    public User getUserById(int id) {
        User user = null;
        String query = "{CALL get_user_by_id(?)}";

        try (Connection connection = getConnection();
                CallableStatement callableStatement = connection.prepareCall(query)) {

            callableStatement.setInt(1, id);
            ResultSet rs = callableStatement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                user = new User(id, name, email, country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public void insertUserStore(User user) throws SQLException {
        String query = "{CALL insert_user(?, ?, ?)}";

        try (Connection connection = getConnection();
                CallableStatement callableStatement = connection.prepareCall(query)) {

            callableStatement.setString(1, user.getName());
            callableStatement.setString(2, user.getEmail());
            callableStatement.setString(3, user.getCountry());
            callableStatement.executeUpdate();
        }
    }



    @Override
    public List<User> selectAllUsersStore() {
        List<User> users = new ArrayList<>();
        String query = "{CALL get_all_users()}";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(query);
             ResultSet rs = callableStatement.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("country")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public boolean updateUserStore(User user) throws SQLException {
        String query = "{CALL update_user(?, ?, ?, ?)}";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(query)) {

            callableStatement.setInt(1, user.getId());
            callableStatement.setString(2, user.getName());
            callableStatement.setString(3, user.getEmail());
            callableStatement.setString(4, user.getCountry());
            return callableStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteUserStore(int id) throws SQLException {
        String query = "{CALL delete_user(?)}";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(query)) {

            callableStatement.setInt(1, id);
            return callableStatement.executeUpdate() > 0;
        }
    }


    @Override
    public void addUserTransaction(User user, int[] permissionIds) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtAssignment = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            String insertUserSql = "INSERT INTO users (name, email, country) VALUES (?, ?, ?)";
            pstmtUser = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
            pstmtUser.setString(1, user.getName());
            pstmtUser.setString(2, user.getEmail());
            pstmtUser.setString(3, user.getCountry());
            pstmtUser.executeUpdate();

            rs = pstmtUser.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            if (permissionIds != null && permissionIds.length > 0) {
                String insertPermissionSql =
                        "INSERT INTO user_permission (user_id, permission_id_error) VALUES (?, ?)";
                pstmtAssignment = connection.prepareStatement(insertPermissionSql);

                for (int permissionId : permissionIds) {
                    pstmtAssignment.setInt(1, userId);
                    pstmtAssignment.setInt(2, permissionId);
                    pstmtAssignment.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (pstmtUser != null) pstmtUser.close();
            if (pstmtAssignment != null) pstmtAssignment.close();
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }


    @Override
    public void insertUpdateWithoutTransaction() throws SQLException {
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement();
             PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT);
             PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) {

            statement.execute(SQL_TABLE_DROP);
            statement.execute(SQL_TABLE_CREATE);

            psInsert.setString(1, "Quynh");
            psInsert.setBigDecimal(2, new BigDecimal(10));
            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            psInsert.execute();

            psInsert.setString(1, "Ngan");
            psInsert.setBigDecimal(2, new BigDecimal(20));
            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            psInsert.execute();

            psUpdate.setBigDecimal(2, new BigDecimal(999.99));
            psUpdate.setString(2, "Quynh");
            psUpdate.execute();

        } catch (Exception e) {
            System.out.println("Đã bắt được lỗi trong quá trình thực thi SQL:");
            e.printStackTrace();
        }
    }


    @Override
    public void insertUpdateUseTransaction() {
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement();
             PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT);
             PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) {

            statement.execute(SQL_TABLE_DROP);
            statement.execute(SQL_TABLE_CREATE);

            conn.setAutoCommit(false);

            psInsert.setString(1, "Quynh");
            psInsert.setBigDecimal(2, new BigDecimal(10));
            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            psInsert.execute();

            psInsert.setString(1, "Ngan");
            psInsert.setBigDecimal(2, new BigDecimal(20));
            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            psInsert.execute();

            // psUpdate.setBigDecimal(2, new BigDecimal(999.99));
            psUpdate.setBigDecimal(1, new BigDecimal(999.99));
            psUpdate.setString(2, "Quynh");
            psUpdate.execute();

            conn.commit();
            conn.setAutoCommit(true);

        } catch (Exception e) {
            System.out.println("Lỗi xảy ra, Transaction sẽ tự động huỷ bỏ khi đóng kết nối!");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private List<User> getUsers(String sql) {
        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("country")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
}
