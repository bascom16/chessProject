package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebSocket
public class WebSocketHandler {

    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    Logger log = Logger.getLogger("serverLogger");

    public WebSocketHandler(AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = readCommand(message);
            log.info("WebSocketHandler received command " + command.getCommandType());
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove((MakeMoveCommand) command);
                case LEAVE -> leave((LeaveCommand) command);
                case RESIGN -> resign((ResignCommand) command);
            }
        } catch (Exception ex) {
            handleException(session, ex);
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

    private void handleException(Session session, Exception ex) {
        try {
            if (session.isOpen()) {
                ErrorMessage message = new ErrorMessage(ex.getMessage());
                session.getRemote().sendString(new ErrorMessage(ex.getMessage()).toString());
            }
        } catch (Exception e) {
            log.warning("Could not send error message " + ex.getMessage() + e.getMessage());
        }
    }

    private void connect(Session session, ConnectCommand command) throws IOException {
        String username = authenticate(command.getAuthToken()).username();
        String color = getUserColor(username, command.getGameID());
        GameData game = getGame(command.getGameID());
        connectionManager.add(username, session);
        log.info("WebSocketHandler connected " + username);
        NotificationMessage broadcastMessage = new NotificationMessage(
                String.format("[%s] has joined the game as %s. Welcome!", username, color));
        connectionManager.broadcast(username, broadcastMessage);
        LoadGameMessage loadMessage = new LoadGameMessage(game);
        connectionManager.sendToUser(username, loadMessage);
    }

    private String getUserColor(String username, Integer gameID) throws IOException {
        GameData gameData = getGame(gameID);
        if (gameData == null) {
            log.warning("Game not found");
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
            log.warning("Data access error: Game not found" + ex.getMessage());
            throw new IOException(ex.getMessage());
        }
    }

    private void makeMove(MakeMoveCommand command) throws IOException {
        log.info("Websocket received move command " + command.getMove());
        validateGameNotOver(command.getGameID());
        String username = authenticate(command.getAuthToken()).username();
        validateNotObserver(username, command.getGameID());
        validateMyTurn(username, command.getGameID());
        ChessMove move = command.getMove();
        ChessGame game = getGame(command.getGameID()).game();
        try {
            game.makeMove(move);
        } catch (InvalidMoveException ex) {
            log.info("Server received invalid move. " + ex.getMessage());
            connectionManager.sendToUser(username, new ErrorMessage(ex.getMessage()));
            log.info("Sent invalid move message");
            return;
        } catch (NullPointerException ex) {
            log.log(Level.WARNING, "Null pointer exception in makeMove", ex);
            connectionManager.sendToUser(username, new ErrorMessage("Hmmm, something went wrong. Try again."));
            log.info("Send null pointer error message");
            return;
        }
        GameData updatedGameData = updateGameData(command.getGameID(), game);
        log.fine("Updated game data in database for move " + move);

        log.fine("Broadcasting load message");
        connectionManager.broadcast(null, new LoadGameMessage(updatedGameData));
        log.fine("Sent load message");

        log.fine("Broadcasting move notification");
        connectionManager.broadcast(username, new NotificationMessage
                (String.format("[%s] made move %s.", username, move.toSimpleString())));
        log.fine("Sent move notification");

        checkEndOfTurnConditions(command, username, game, updatedGameData);
    }

private void checkEndOfTurnConditions(MakeMoveCommand command,
                                      String username,
                                      ChessGame game,
                                      GameData updatedGameData) throws IOException {
        ChessGame.TeamColor otherUserColor = getUserColor(username, command.getGameID()).equals("White")
                ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String otherUser = Objects.equals(username, updatedGameData.whiteUsername())
                ? updatedGameData.blackUsername() : updatedGameData.whiteUsername();

        if (game.isInCheckmate(otherUserColor)) {
            log.info("Game is in checkmate");
            game.setGameOver();
            connectionManager.broadcast(null, new NotificationMessage
                    (String.format("Checkmate! [%s] wins.", username)));
        } else if (game.isInStalemate(otherUserColor)) {
            log.info("Game is in stalemate");
            game.setGameOver();
            connectionManager.broadcast(null, new NotificationMessage
                    (String.format("[%s] cannot move. The game ends in a stalemate.", otherUser)));
        } else if (game.isInCheck(otherUserColor)) {
            log.info("Game is in check");
            connectionManager.broadcast(null, new NotificationMessage
                    (String.format("[%s] is in Check!", otherUser)));
        }
    }

    private GameData updateGameData(int gameID, ChessGame game) throws IOException {
        GameData oldData = getGame(gameID);
        GameData newData = new GameData(gameID, oldData.whiteUsername(), oldData.blackUsername(), oldData.gameName(), game);
        try {
            gameDataAccess.update(newData);
        } catch (DataAccessException ex) {
            log.warning("WebSocketHandler could not update game." + ex.getMessage());
            throw new IOException("Unable to update game");
        }
        return newData;
    }

    private void leave(LeaveCommand command) throws IOException {
        String username = authenticate(command.getAuthToken()).username();
        NotificationMessage message = new NotificationMessage(
                String.format("[%s] has left the game. Goodbye!", username));
        connectionManager.broadcast(username, message);
        connectionManager.remove(username);
        log.info("WebSocketHandler removed connection for " + username);
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
            log.warning("WebSocketHandler could not update game after player left." + ex.getMessage());
            throw new IOException("Unable to update game");
        }

    }

    private void resign(ResignCommand command) throws IOException {
        String username = authenticate(command.getAuthToken()).username();
        validateGameNotOver(command.getGameID());
        validateNotObserver(username, command.getGameID());
        GameData gameData = getGame(command.getGameID());
        String otherUser = (Objects.equals(username, gameData.whiteUsername()))
                ? gameData.blackUsername() : gameData.whiteUsername();
        ChessGame finishedGame = new ChessGame(gameData.game().getBoard());
        finishedGame.setGameOver();
        updateGameData(command.getGameID(), finishedGame);
        log.info(String.format("User %s has resigned. Game over.", username));
        NotificationMessage message = new NotificationMessage(
                String.format("[%s] has resigned. [%s] wins!", username, otherUser));
        connectionManager.broadcast(null, message);
    }

    private AuthData authenticate(String authToken) throws IOException {
        try {
            AuthData authData = authDataAccess.read(authToken);
            if (authData != null) {
                log.fine(String.format("Authenticated user %s", authData.username()));
                return authData;
            }
            log.warning("Unauthorized user");
            throw new IOException("User is not authorized");
        } catch (DataAccessException ex) {
            log.warning("Authenticate Data Access error" + ex.getMessage());
            throw new IOException("User is not authorized: Data Access Error");
        }
    }

    private void validateGameNotOver(int gameID) throws IOException {
        GameData gameData = getGame(gameID);
         if (gameData.game().isGameOver()) {
             log.info("Game over error.");
             throw new IOException("Game is over.");
         }
    }

    private void validateNotObserver(String username, int gameID) throws IOException {
        GameData gameData = getGame(gameID);
        if (    !Objects.equals(username, gameData.whiteUsername()) &&
                !Objects.equals(username, gameData.blackUsername())) {
            throw new IOException("Observer cannot participate.");
        }
    }

    private void validateMyTurn(String username, int gameID) throws IOException {
        GameData gameData = getGame(gameID);
        ChessGame.TeamColor teamTurn = gameData.game().getTeamTurn();
        if (teamTurn == ChessGame.TeamColor.WHITE) {
            if (Objects.equals(username, gameData.whiteUsername())) {
                return;
            }
        } else if (teamTurn == ChessGame.TeamColor.BLACK) {
            if (Objects.equals(username, gameData.blackUsername())) {
                return;
            }
        }
        throw new IOException("It's not your turn!");
    }
}
