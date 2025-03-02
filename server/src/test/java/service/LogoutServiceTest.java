package service;

import exception.ResponseException;
import handler.request.AuthorizationRequest;
import handler.request.LoginRequest;
import handler.result.LoginResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest extends BaseServiceTest {

    @Test
    @DisplayName("Logout success")
    void logoutSuccess() {
        String authToken = register("username", "password", "email");

        assertDoesNotThrow( () -> service.logout(new AuthorizationRequest(authToken)));

        assertNull(authDataAccess.read(authToken));
    }

    @Test
    @DisplayName("Unauthorized logout failure")
    void unauthorizedLogoutFail() {
        String authToken = register("username", "password", "email");

        assertThrows(ResponseException.class, () -> service.logout(new AuthorizationRequest("wrongToken")));

        assertNotNull(authDataAccess.read(authToken));
    }

    @Test
    @DisplayName("Bad request logout failure")
    void badRequestLogoutFail() {
        String authToken = register("username", "password", "email");

        assertThrows(ResponseException.class, () -> service.logout(new AuthorizationRequest(null)));
        assertNotNull(authDataAccess.read(authToken));
    }
}