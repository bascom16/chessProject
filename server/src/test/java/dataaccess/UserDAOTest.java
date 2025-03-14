package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDAOTest extends DAOTest<UserData, String>{
    @BeforeEach
    void setUp() {
        try {
            dataAccessObject = new MySQLUserDAO();
            dataAccessObject.reset();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }

        identifier = "username";
        identifier2 = "username2";
        identifier3 = "username3";
        data = new UserData(identifier, "password", "email");
        data2 = new UserData(identifier2, "password2", "email2");
        data3 = new UserData(identifier3, "password3", "email3");
    }

    @Override
    @Test
    void updateSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        UserData modifiedData = new UserData(identifier, "differentPassword", "differentEmail");
        assertDoesNotThrow( () -> dataAccessObject.update(modifiedData));
        assertEquals(modifiedData, assertDoesNotThrow( () -> dataAccessObject.read(identifier)) );
    }
}
