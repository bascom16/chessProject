package service;

import exception.ResponseException;
import handler.request.AuthorizationRequest;
import handler.request.CreateRequest;
import handler.request.JoinRequest;
import handler.result.CreateResult;
import model.GameData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoinServiceTest extends BaseServiceTest{

    @Test
    @DisplayName("Join success")
    void joinSuccess() {
        String authData = register("username", "password", "email");
        AuthorizationRequest authRequest = new AuthorizationRequest(authData);
        CreateRequest createRequest = new CreateRequest("game");

        CreateResult createResult = assertDoesNotThrow( () -> service.create(authRequest, createRequest));

        JoinRequest joinRequest = new JoinRequest("WHITE", createResult.gameID());
        assertDoesNotThrow( () -> service.join(authRequest, joinRequest));
        GameData gameData = assertDoesNotThrow( () -> gameDataAccess.read(createResult.gameID()));
        assertEquals("username", gameData.whiteUsername());
    }

    @Test
    @DisplayName("Join unauthorized failure")
    void joinUnauthorizedFailure() {
        String authData = register("username", "password", "email");
        AuthorizationRequest authRequest = new AuthorizationRequest(authData);
        CreateRequest createRequest = new CreateRequest("game");

        CreateResult createResult = assertDoesNotThrow( () -> service.create(authRequest, createRequest));

        JoinRequest joinRequest = new JoinRequest("WHITE", createResult.gameID());
        AuthorizationRequest authRequest2 = new AuthorizationRequest("try this");
        assertThrows(ResponseException.class, () -> service.join(authRequest2, joinRequest));
        GameData gameData = assertDoesNotThrow( () -> gameDataAccess.read(createResult.gameID()));
        assertNotEquals("username", gameData.whiteUsername());
    }

    @Test
    @DisplayName("Join Already taken failure")
    void joinAlreadyTakenFailure() {
        String authData1 = register("username1", "password1", "email1");
        AuthorizationRequest authRequest1 = new AuthorizationRequest(authData1);

        CreateRequest createRequest = new CreateRequest("game");
        CreateResult createResult = assertDoesNotThrow( () -> service.create(authRequest1, createRequest));

        JoinRequest joinRequest1 = new JoinRequest("WHITE", createResult.gameID());
        assertDoesNotThrow( () -> service.join(authRequest1, joinRequest1));

        String authData2 = register("username2", "password2", "email2");
        AuthorizationRequest authRequest2 = new AuthorizationRequest(authData2);

        JoinRequest joinRequest2 = new JoinRequest("WHITE", createResult.gameID());
        assertThrows(ResponseException.class, () -> service.join(authRequest2, joinRequest2));

        GameData gameData = assertDoesNotThrow( () -> gameDataAccess.read(createResult.gameID()));
        assertEquals("username1", gameData.whiteUsername());
    }
}