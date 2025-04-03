package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public WebSocketHandler(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = readCommand(message);
        switch (command.getCommandType()) {
            case CONNECT -> connect(session, (ConnectCommand) command);
            case MAKE_MOVE -> makeMove(session, (MakeMoveCommand) command);
            case LEAVE -> leave((LeaveCommand) command);
            case RESIGN -> resign(session, (ResignCommand) command);
        }
    }

    private UserGameCommand readCommand(String message) {
        Gson gson = new Gson();
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        return switch (command.getCommandType()) {
            case CONNECT -> gson.fromJson(message, ConnectCommand.class);
            case MAKE_MOVE -> gson.fromJson(message, MakeMoveCommand.class);
            case LEAVE -> gson.fromJson(message, LeaveCommand.class);
            case RESIGN -> gson.fromJson(message, ResignCommand.class);
        };
    }

    private void connect(Session session, ConnectCommand command) throws IOException {
        String username = authenticate(command.getAuthToken()).username();
        String color = getUserColor(username, command.getGameID());
        GameData game = getGame(command.getGameID());
        connectionManager.add(username, session);
        NotificationMessage broadcastMessage = new NotificationMessage(
                String.format("[%s] has joined the game as %s. Welcome!", username, color));
        connectionManager.broadcast(username, broadcastMessage);
        LoadGameMessage loadMessage = new LoadGameMessage(game);
        connectionManager.sendToUser(username, loadMessage);
    }

    private String getUserColor(String username, Integer gameID) throws IOException {
        GameData gameData = getGame(gameID);
        if (gameData == null) {
            throw new IOException("Game not found");
        }
        if (Objects.equals(gameData.whiteUsername(), username)) {
            return "White";
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            return "Black";
        } else {
            return "Observer";
        }
    }

    private GameData getGame(Integer gameID) throws IOException {
        try {
            return gameDataAccess.read(gameID);
        } catch (DataAccessException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    private void makeMove(Session session, MakeMoveCommand command) {
//        TODO: VERIFY MOVE VALIDITY

//        TODO: UPDATE GAME IN DATABASE

//        TODO: SEND LOAD AND NOTIFICATION

//        TODO: CHECK FOR CHECK/CHECKMATE AND NOTIFY
        throw new RuntimeException("not implemented");
    }

    private void leave(LeaveCommand command) throws IOException {
        String username = authenticate(command.getAuthToken()).username();
        connectionManager.remove(username);
        NotificationMessage message = new NotificationMessage(
                String.format("[%s] has left the game. Goodbye!", username));
        connectionManager.broadcast(username, message);
        GameData gameData = getGame(command.getGameID());
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        if (Objects.equals(whiteUsername, username)) {
            whiteUsername = null;
        }
        if (Objects.equals(blackUsername, username)) {
            blackUsername = null;
        }
        GameData updatedGameData =
                new GameData(gameData.gameID(), whiteUsername, blackUsername, gameData.gameName(), gameData.game());
        try {
            gameDataAccess.update(updatedGameData);
        } catch (DataAccessException ex) {
            throw new IOException("Unable to update game");
        }
//        TODO: CHECK IF THIS ACTUALLY WORKS
    }

    private void resign(Session session, ResignCommand command) {
//        TODO: IMPLEMENT RESIGN
//        TODO: MARK GAME AS OVER
//        TODO: NOTIFY OF RESIGN
        throw new RuntimeException("not implemented");
    }

    // returns username
    private AuthData authenticate(String authToken) throws IOException {
        try {
            AuthData authData = authDataAccess.read(authToken);
            if (authData != null) {
                return authData;
            }
            throw new IOException("User is not authorized");
        } catch (DataAccessException ex) {
            throw new IOException("User is not authorized: Data Access Error");
        }
    }

    private void handleException(Exception ex) throws IOException {
        throw new IOException(ex.getMessage());
    }
}
