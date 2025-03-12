package service;

import dataaccess.*;
import handler.request.RegisterRequest;
import handler.result.RegisterResult;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BaseServiceTest {
    protected UserDAO userDataAccess;
    protected AuthDAO authDataAccess;
    protected GameDAO gameDataAccess;

    protected static ServiceManager service;

    @BeforeEach
    void setUp() {
        try {
            userDataAccess = new MySQLUserDAO();
            authDataAccess = new MySQLAuthDAO();
            gameDataAccess = new MySQLGameDAO();
            userDataAccess.reset();
            authDataAccess.reset();
            gameDataAccess.reset();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException("Failed to initialize database");
        }

        service = new ServiceManager(userDataAccess, authDataAccess, gameDataAccess);
    }

    protected String register(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = assertDoesNotThrow( () -> service.register(request));
        return result.authToken();
    }
}
