package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthDAOTest extends DAOTest<AuthData, String>{
    @BeforeEach
    void setUp() {
        try {
            dataAccessObject = new MySQLAuthDAO();
            dataAccessObject.reset();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }

        identifier = "authToken";
        identifier2 = "authToken2";
        identifier3 = "authToken3";
        data = new AuthData(identifier, "username");
        data2 = new AuthData(identifier2, "username2");
        data3 = new AuthData(identifier3, "username3");
    }

    @Override
    @Test
    void updateSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        AuthData modifiedData = new AuthData(identifier, "differentUsername");
        assertDoesNotThrow( () -> dataAccessObject.update(modifiedData));
        assertEquals(modifiedData, assertDoesNotThrow( () -> dataAccessObject.read(identifier)) );
    }
}
