package dataaccess;

import model.UserData;

import java.sql.*;
import java.util.Collection;
import java.util.List;

public class MySQLUserDAO extends MySQLDAO implements UserDAO {
    public MySQLUserDAO() throws DataAccessException {
        super();
    }

    @Override
    public void create(UserData userData) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to create user");
        }
    }

    @Override
    public UserData read(String username) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM user WHERE user=?";
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
        return new UserData(username, password, email);
    }

    @Override
    public Collection<UserData> readAll() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void update(UserData userData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void delete(UserData userData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException("not implemented");
    }
}
