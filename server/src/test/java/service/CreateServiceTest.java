package service;

import exception.ResponseException;
import handler.request.AuthorizationRequest;
import handler.request.CreateRequest;
import handler.result.CreateResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateServiceTest extends BaseServiceTest{

    @Test
    @DisplayName("Create Success")
    void createSuccess() {
        String authData = register("username", "password", "email");
        AuthorizationRequest authRequest = new AuthorizationRequest(authData);
        CreateRequest createRequest = new CreateRequest("game");

        CreateResult result = assertDoesNotThrow( () -> service.create(authRequest, createRequest));
        assertNotNull(gameDataAccess.read(result.gameID()));
    }

    @Test
    @DisplayName("Multiple Game Create Success")
    void multipleGameCreateSuccess() {
        String authData = register("username", "password", "email");
        AuthorizationRequest authRequest = new AuthorizationRequest(authData);
        CreateRequest createRequest1 = new CreateRequest("game1");
        CreateRequest createRequest2 = new CreateRequest("game2");

        CreateResult result1 = assertDoesNotThrow( () -> service.create(authRequest, createRequest1));
        assertNotNull(gameDataAccess.read(result1.gameID()));
        CreateResult result2 = assertDoesNotThrow( () -> service.create(authRequest, createRequest2));
        assertNotNull(gameDataAccess.read(result2.gameID()));
    }

    @Test
    @DisplayName("Multiple User Create Success")
    void multipleUserCreateSuccess() {
        String authData1 = register("username", "password", "email");
        String authData2 = register("username2", "password2", "email2");
        AuthorizationRequest authRequest1 = new AuthorizationRequest(authData1);
        AuthorizationRequest authRequest2 = new AuthorizationRequest(authData2);
        CreateRequest createRequest1 = new CreateRequest("game1");
        CreateRequest createRequest2 = new CreateRequest("game2");

        CreateResult result1 = assertDoesNotThrow( () -> service.create(authRequest1, createRequest1));
        assertNotNull(gameDataAccess.read(result1.gameID()));
        CreateResult result2 = assertDoesNotThrow( () -> service.create(authRequest2, createRequest2));
        assertNotNull(gameDataAccess.read(result2.gameID()));
    }

    @Test
    @DisplayName("Create Unauthorized Failure")
    void createUnauthorized() {
        register("username", "password", "email");
        AuthorizationRequest authRequest = new AuthorizationRequest("nope");
        CreateRequest createRequest = new CreateRequest("null");

        assertThrows(ResponseException.class, () -> service.create(authRequest, createRequest));
    }
}