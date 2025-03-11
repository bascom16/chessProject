package dataaccess;

import java.sql.*;

public class MySQLDAO {
    MySQLDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    static Connection getConnection() throws DataAccessException {
        return DatabaseManager.getConnection();
    }
}
