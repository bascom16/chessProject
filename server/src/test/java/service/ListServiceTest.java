package service;

import exception.ResponseException;
import handler.request.AuthorizationRequest;
import handler.request.CreateRequest;
import handler.result.CreateResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListServiceTest extends BaseServiceTest{

    @Test
    @DisplayName("List success")
    void listSuccess() {
        String authData = register("username", "password", "email");
        AuthorizationRequest authRequest = new AuthorizationRequest(authData);
        CreateRequest createRequest1 = new CreateRequest("game1");
        CreateRequest createRequest2 = new CreateRequest("game2");
        CreateRequest createRequest3 = new CreateRequest("game3");

        CreateResult result1 = assertDoesNotThrow( () -> service.create(authRequest, createRequest1));
        CreateResult result2 = assertDoesNotThrow( () -> service.create(authRequest, createRequest2));
        CreateResult result3 = assertDoesNotThrow( () -> service.create(authRequest, createRequest3));
        assertNotNull(assertDoesNotThrow( () -> gameDataAccess.read(result1.gameID())));
        assertNotNull(assertDoesNotThrow( () -> gameDataAccess.read(result2.gameID())));
        assertNotNull(assertDoesNotThrow( () -> gameDataAccess.read(result3.gameID())));
        assertDoesNotThrow( () -> service.list(authRequest));
    }

    @Test
    @DisplayName("List fail")
    void listFailure() {
        String authData = register("username", "password", "email");
        AuthorizationRequest authRequest = new AuthorizationRequest(authData);
        CreateRequest createRequest1 = new CreateRequest("game1");
        CreateRequest createRequest2 = new CreateRequest("game2");
        CreateRequest createRequest3 = new CreateRequest("game3");

        assertDoesNotThrow( () -> service.create(authRequest, createRequest1));
        assertDoesNotThrow( () -> service.create(authRequest, createRequest2));
        assertDoesNotThrow( () -> service.create(authRequest, createRequest3));
        AuthorizationRequest authRequest2 = new AuthorizationRequest("haha no");
        assertThrows(ResponseException.class, () -> service.list(authRequest2));
    }
}