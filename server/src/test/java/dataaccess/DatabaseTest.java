package dataaccess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseTest {

    @Test
    void resetDatabase() {
        MySQLUserDAO userDAO;
        MySQLAuthDAO authDAO;
        MySQLGameDAO gameDAO;
        try {
            userDAO = new MySQLUserDAO();
            userDAO.reset();
            authDAO = new MySQLAuthDAO();
            authDAO.reset();
            gameDAO = new MySQLGameDAO();
            gameDAO.reset();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
        assertEquals(0, assertDoesNotThrow( () -> userDAO.readAll().size()));
        assertEquals(0, assertDoesNotThrow( () -> authDAO.readAll().size()));
        assertEquals(0, assertDoesNotThrow( () -> gameDAO.readAll().size()));
    }
}
