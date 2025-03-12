package dataaccess;

import model.AuthData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLAuthDAO extends MySQLDAO implements AuthDAO {
    public MySQLAuthDAO() throws DataAccessException {
        super();
    }

    @Override
    public void create(AuthData authData) throws DataAccessException {
        if (authData == null) {
            throw new DataAccessException("Unable to create authorization: invalid input");
        }
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.username());
                preparedStatement.setString(2, authData.authToken());
                preparedStatement.executeUpdate();
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
    public Collection<AuthData> readAll() throws DataAccessException {
        Collection<AuthData> authData = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM auth;";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        authData.add(readAuthData(resultSet));
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to read authorizations");
        }
        return authData;
    }

    @Override
    public void update(AuthData authData) throws DataAccessException {
        if (!isInDatabase(authData)) {
            throw new DataAccessException("Unable to update authorization: authorization not found");
        }
        AuthData oldUserData = read(authData.authToken());
        delete(oldUserData);
        create(authData);
    }

    @Override
    public void delete(AuthData authData) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            if (!isInDatabase(authData)) {
                throw new DataAccessException("Unable to delete authorization: authorization not found");
            }
            String statement = "DELETE FROM auth WHERE authToken=?;";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to delete authorization");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        String statement = "TRUNCATE TABLE auth;";
        executeBasicStatement(statement, "Unable to delete auth data");
    }

    @Override
    public void reset() throws DataAccessException {
        String statement = "DROP TABLE auth;";
        executeBasicStatement(statement, "Unable to reset authorization table");
        configureDatabase();
    }

    private Boolean isInDatabase(AuthData data) throws DataAccessException {
        return read(data.authToken()) != null;
    }
}
