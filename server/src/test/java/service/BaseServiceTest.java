package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;

public class BaseServiceTest {
    protected UserDAO userDataAccess;
    protected AuthDAO authDataAccess;
    protected GameDAO gameDataAccess;

    protected static ServiceManager service;

    @BeforeEach
    void setUp() {
        userDataAccess = new MemoryUserDAO();
        authDataAccess = new MemoryAuthDAO();
        gameDataAccess = new MemoryGameDAO();

        service = new ServiceManager(userDataAccess, authDataAccess, gameDataAccess);
    }
}
