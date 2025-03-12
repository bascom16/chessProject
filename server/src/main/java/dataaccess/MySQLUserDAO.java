package dataaccess;

import model.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLUserDAO extends MySQLDAO implements UserDAO {
    public MySQLUserDAO() throws DataAccessException {
        super();
    }

    @Override
    public void create(UserData userData) throws DataAccessException {
        if (userData == null) {
            throw new DataAccessException("Unable to create user: invalid input");
        }
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to create user");
        }
    }

    @Override
    public UserData read(String username) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM user WHERE username=?;";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return readUserData(resultSet);
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to read user");
        }
        return null;
    }

    private UserData readUserData(ResultSet resultSet) throws SQLException {
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String email = resultSet.getString("email");
        if (username == null || password == null || email == null) {
            return null;
        }
        return new UserData(username, password, email);
    }

    @Override
    public Collection<UserData> readAll() throws DataAccessException {
        Collection<UserData> userData = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM user;";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        userData.add(readUserData(resultSet));
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to read user");
        }
        return userData;
    }

    @Override
    public void update(UserData userData) throws DataAccessException {
        if (!isInDatabase(userData)) {
            throw new DataAccessException("Unable to update user: user not found");
        }
        UserData oldUserData = read(userData.username());
        delete(oldUserData);
        create(userData);
    }

    @Override
    public void delete(UserData userData) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            if (!isInDatabase(userData)) {
                throw new DataAccessException("Unable to delete user: user not found");
            }
            String statement = "DELETE FROM user WHERE username=?;";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to delete user");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        String statement = "TRUNCATE TABLE user;";
        executeBasicStatement(statement, "Unable to delete user data");
    }

    public void reset() throws DataAccessException {
        String statement = "DROP TABLE user;";
        executeBasicStatement(statement, "Unable to reset user table");
        configureDatabase();
    }

    private Boolean isInDatabase(UserData data) throws DataAccessException {
        return read(data.username()) != null;
    }
}
