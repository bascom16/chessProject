package dataaccess;

import java.sql.*;

public class MySQLDAO {
    MySQLDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        configureDatabase();
    }

    protected void configureDatabase() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to configure database");
        }
    }

    private final String[] createStatements = {
    """
    CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    email VARCHAR(256) NOT NULL,
    PRIMARY KEY (username)
    );
    """,
    """
    CREATE TABLE IF NOT EXISTS auth (
    authToken VARCHAR(256) NOT NULL,
    username VARCHAR(256) NOT NULL,
    PRIMARY KEY (authToken)
    );
    """,
    """
    CREATE TABLE IF NOT EXISTS game (
    gameID INT NOT NULL,
    whiteUser VARCHAR(256) DEFAULT NULL,
    blackUser VARCHAR(256) DEFAULT NULL,
    gameName VARCHAR(256) NOT NULL,
    gameData TEXT DEFAULT NULL,
    PRIMARY KEY (gameID)
    );
    """
    };

    protected void executeBasicStatement(String statement, String failureMessage) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException(failureMessage);
        }
    }
}
