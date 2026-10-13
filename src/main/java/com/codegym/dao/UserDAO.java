package com.codegym.dao;

import com.codegym.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private final String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=UTC";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "password";

    private static final String INSERT_USERS_SQL =
            "INSERT INTO users (name, email, country) VALUES (?, ?, ?);";
    private static final String SELECT_USER_BY_ID =
            "SELECT id, name, email, country FROM users WHERE id = ?;";
    private static final String SELECT_ALL_USERS =
            "SELECT * FROM users;";
    private static final String DELETE_USERS_SQL =
            "DELETE FROM users WHERE id = ?;";
    private static final String UPDATE_USERS_SQL =
            "UPDATE users SET name = ?, email = ?, country = ? WHERE id = ?;";
    private static final String SEARCH_BY_COUNTRY_SQL =
            "SELECT * FROM users WHERE country LIKE ?;";
    private static final String SORT_BY_NAME_SQL =
            "SELECT * FROM users ORDER BY name ASC;";

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
    public void insertUser(User user) throws Exception {
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
        return getUsers(SELECT_ALL_USERS);
    }

    @Override
    public boolean deleteUser(int id) throws Exception {
        boolean rowDeleted;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL)) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }

        return rowDeleted;
    }

    @Override
    public boolean updateUser(User user) throws Exception {
        boolean rowUpdated;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            statement.setInt(4, user.getId());
            rowUpdated = statement.executeUpdate() > 0;
        }

        return rowUpdated;
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
                        rs.getString("country")
                ));
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
                        rs.getString("country")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
}
