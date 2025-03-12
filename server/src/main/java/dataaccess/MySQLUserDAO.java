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
        try (Connection connection = DatabaseManager.getConnection();) {
            String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = connection.prepareStatement(statement);) {
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
    public UserData read(String username) {
        throw new RuntimeException("not implemented");
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
