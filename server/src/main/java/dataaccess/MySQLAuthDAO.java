package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
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
            try (var preparedStatement = connection.prepareStatement(statement);) {
                preparedStatement.setString(1, authData.username());
                preparedStatement.setString(2, authData.authToken());
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to create authorization");
        }
    }

    @Override
    public AuthData read(String authToken) {
        throw new RuntimeException("not implemented");
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
}
