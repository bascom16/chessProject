package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AuthDAOTest extends DAOTest<AuthData>{
    @BeforeEach
    void setUp() {
        dataAccessObject = new MemoryAuthDAO();
        identifier = "authToken";
        identifier2 = "authToken2";
        identifier3 = "authToken3";
        data = new AuthData(identifier, "username");
        data2 = new AuthData(identifier2, "username2");
        data3 = new AuthData(identifier3, "username3");
    }

    @Override
    void updateSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        AuthData modifiedData = new AuthData("differentAuthToken", identifier);
        assertDoesNotThrow( () -> dataAccessObject.update(modifiedData));
    }
}
