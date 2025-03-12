package service;

import handler.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest extends BaseServiceTest{

    @Test
    @DisplayName("Single Clear Success")
    void singleClearSuccess() {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        assertDoesNotThrow( () -> service.register(request));

        assertDoesNotThrow( () -> service.clear());

        assertNull(assertDoesNotThrow( () -> userDataAccess.read("username")));
        assertNull(assertDoesNotThrow( () -> authDataAccess.read("username")));
    }

    @Test
    @DisplayName("Multiple Clear Success")
    void multipleClearSuccess() {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
       assertDoesNotThrow( () ->  service.register(request));

        assertDoesNotThrow( () -> {
            service.clear();
            service.clear();
            service.clear();
        });

        assertNull(assertDoesNotThrow( () -> userDataAccess.read("username")) );
        assertNull(assertDoesNotThrow( () -> authDataAccess.read("username")) );
    }
}