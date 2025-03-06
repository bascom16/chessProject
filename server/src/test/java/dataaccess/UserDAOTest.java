package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserDAOTest extends DAOTest<UserData>{
    @BeforeEach
    void setUp() {
        dataAccessObject = new MemoryUserDAO();
        identifier = "username";
        identifier2 = "username2";
        identifier3 = "username3";
        data = new UserData(identifier, "password", "email");
        data2 = new UserData(identifier2, "password2", "email2");
        data3 = new UserData(identifier3, "password3", "email3");
    }

    @Override
    void updateSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        UserData modifiedData = new UserData(identifier, "password", "differentEmail");
        assertDoesNotThrow( () -> dataAccessObject.update(modifiedData));
    }
}
