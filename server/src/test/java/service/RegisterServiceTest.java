package service;

import dataaccess.*;
import handler.request.RegisterRequest;
import handler.result.RegisterResult;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Provider;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTest {

    private static UserDAO userDataAccess;
    private static AuthDAO authDataAccess;
    private static GameDAO gameDataAccess;

    private static ServiceManager service;

    @BeforeEach
    void setUp() {
        userDataAccess = new MemoryUserDAO();
        authDataAccess = new MemoryAuthDAO();
        gameDataAccess = new MemoryGameDAO();

        service = new ServiceManager(userDataAccess, authDataAccess, gameDataAccess);
    }

    @Test
    @DisplayName("Single Register Success")
    void singleRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterResult result = service.register(request);

        assertEquals("username", result.username());

        UserData userData = userDataAccess.read("username");
        assertEquals("username", userData.username());
        assertEquals("password", userData.password());
        assertEquals("email", userData.email());

        AuthData authData = authDataAccess.read("username");
        assertEquals(result.authToken(), authData.authToken());
    }

    @Test
    @DisplayName("Double Register Success")
    void doubleRegisterSuccess() {
        RegisterRequest request1 = new RegisterRequest("username1", "password1", "email1");
        RegisterRequest request2 = new RegisterRequest("username2", "password2", "email2");
        RegisterResult result1 = service.register(request1);
        RegisterResult result2 = service.register(request2);

        assertEquals("username1", result1.username());
        assertEquals("username2", result2.username());

        UserData userData1 = userDataAccess.read("username1");
        UserData userData2 = userDataAccess.read("username2");
        assertEquals("username1", userData1.username());
        assertEquals("username2", userData2.username());
        assertEquals("password1", userData1.password());
        assertEquals("password2", userData2.password());
        assertEquals("email1", userData1.email());
        assertEquals("email2", userData2.email());

        AuthData authData1 = authDataAccess.read("username1");
        AuthData authData2 = authDataAccess.read("username2");
        assertEquals(result1.authToken(), authData1.authToken());
        assertEquals(result2.authToken(), authData2.authToken());
    }
}