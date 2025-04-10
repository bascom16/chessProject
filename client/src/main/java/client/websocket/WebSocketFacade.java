package client.websocket;

import chess.ChessMove;
import client.ChessClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ClientException;
import ui.EscapeSequences;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

@ClientEndpoint
public class WebSocketFacade {
    private Session session;
    private final NotificationHandler notificationHandler;
    private final ChessClient client;
    private final URI socketURI;

    Logger log = Logger.getLogger("clientLogger");

    public WebSocketFacade(String url, NotificationHandler notificationHandler, ChessClient client)
            throws ClientException {
        try {
            this.notificationHandler = notificationHandler;

            url = url.replace("http", "ws");
            this.socketURI = new URI(url + "/ws");

            connectToServer();

            this.client = client;
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            log.warning("WebSocketFacade creation failed. " + ex.getMessage());
            throw new ClientException(500, ex.getMessage());
        }
    }

    private void connectToServer() throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, socketURI);
        log.info("Created session and connected to server");
    }

    @OnMessage
    public void onMessage(String message) {
        JsonObject messageObject = JsonParser.parseString(message).getAsJsonObject();
        ServerMessage.ServerMessageType serverMessageType
                = ServerMessage.ServerMessageType.valueOf(messageObject.get("serverMessageType").getAsString());
        log.info(String.format("WebSocketFacade received %s message", serverMessageType));

        switch (serverMessageType) {
            case NOTIFICATION -> handleNotification(new Gson().fromJson(messageObject, NotificationMessage.class));
            case ERROR -> handleError(new Gson().fromJson(messageObject, ErrorMessage.class));
            case LOAD_GAME -> handleLoadGame(new Gson().fromJson(messageObject, LoadGameMessage.class));
        }
    }

    public void connect(String authToken, int gameID) throws ClientException {
        if (!session.isOpen()) {
            try {
                connectToServer();
            } catch (Exception ex) {
                log.info(String.format("Unable to create new session: %s", ex.getMessage()));
                throw new ClientException(500, "Unable to create new session");
            }
        }
        sendCommand(new ConnectCommand(authToken, gameID));
    }

    public void leave(String authToken, int gameID) throws ClientException {
        sendCommand(new LeaveCommand(authToken, gameID));
        try {
            this.session.close();
        } catch (IOException ex) {
            log.warning("Session could not be closed. " + ex.getMessage());
            throw new ClientException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ClientException {
        sendCommand(new MakeMoveCommand(authToken, gameID, move));
    }

    public void resign(String authToken, int gameID) throws ClientException {
        sendCommand(new ResignCommand(authToken, gameID));
    }

    private void sendCommand(UserGameCommand command) throws ClientException {
        if (!session.isOpen()) {
            log.warning("Session closed.");
            throw new ClientException(500, "Connection has been lost.");
        }
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            log.warning("Bad WebSocketFacade command." + ex.getMessage());
            throw new ClientException(500, ex.getMessage());
        }
    }

    private void handleNotification(NotificationMessage message) {
        notificationHandler.notify(message);
    }

    private void handleError(ErrorMessage message) {
        log.warning(String.format("Notifying %s", message.getErrorMessage()));
        String notifyMessage =  EscapeSequences.SET_TEXT_COLOR_RED +
                                "Error: " +
                                message.getErrorMessage() +
                                EscapeSequences.RESET_TEXT_COLOR;
        notificationHandler.notify(new NotificationMessage(notifyMessage));
    }

    private void handleLoadGame(LoadGameMessage message) {
        client.loadGame(message.getGame());
    }
}
