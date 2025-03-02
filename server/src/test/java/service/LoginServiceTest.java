package service;

import exception.ResponseException;
import handler.request.AuthorizationRequest;
import handler.request.LoginRequest;
import handler.result.LoginResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest extends BaseServiceTest {

    @Test
    @DisplayName("Login success")
    void loginSuccess() {
        String authToken = register("username", "password", "email");

        assertDoesNotThrow( () -> service.logout(new AuthorizationRequest(authToken)));

        LoginResult loginResult = assertDoesNotThrow(
                () -> service.login(new LoginRequest("username", "password")) );
        String authToken2 = loginResult.authToken();

        assertEquals("username", authDataAccess.read(authToken2).username());
    }

    @Test
    @DisplayName("Wrong Password Failure")
    void loginPasswordFail() {
        String authToken = register("username", "password", "email");

        assertDoesNotThrow( () -> service.logout(new AuthorizationRequest(authToken)) );

        assertThrows(ResponseException.class,
                () -> service.login(new LoginRequest("username", "wrongPassword")) );
    }

    @Test
    @DisplayName("Bad Request Failure")
    void loginBadRequestFail() {
        String authToken = register("username", "password", "email");

        assertDoesNotThrow( () -> service.logout(new AuthorizationRequest(authToken)) );

        assertThrows(ResponseException.class,
                () -> service.login(new LoginRequest(null, null)) );
    }


}