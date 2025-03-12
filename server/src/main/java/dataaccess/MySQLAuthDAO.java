package dataaccess;

import model.AuthData;

import java.sql.*;
import java.util.Collection;
import java.util.List;

public class MySQLAuthDAO extends MySQLDAO implements AuthDAO {
    public MySQLAuthDAO() throws DataAccessException {
        super();
    }

    @Override
    public void create(AuthData authData) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.username());
                preparedStatement.setString(2, authData.authToken());
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to create authorization");
        }
    }

    @Override
    public AuthData read(String authToken) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return readAuthData(resultSet);
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to read authorization");
        }
        return null;
    }

    private AuthData readAuthData(ResultSet resultSet) throws SQLException {
        String authToken = resultSet.getString("authToken");
        String username = resultSet.getString("username");
        return new AuthData(authToken, username);
    }

    @Override
    public Collection<AuthData> readAll() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void update(AuthData authData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void delete(AuthData authData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void reset() throws DataAccessException {
        String statement = "DROP TABLE auth;";
        executeBasicStatement(statement, "Unable to reset authorization table");
        configureDatabase();
    }
}
