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
public class WebSocketFacade extends Endpoint {
    private final Session session;
    private final NotificationHandler notificationHandler;
    private final ChessClient client;

    Logger log = Logger.getLogger("clientLogger");

    public WebSocketFacade(String url, NotificationHandler notificationHandler, ChessClient client)
            throws ClientException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.client = client;
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            log.warning("WebSocketFacade creation failed. " + ex.getMessage());
            throw new ClientException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    @OnMessage
    public void onMessage(String message) {
        JsonObject messageObject = JsonParser.parseString(message).getAsJsonObject();
        ServerMessage.ServerMessageType serverMessageType
                = ServerMessage.ServerMessageType.valueOf(messageObject.get("serverMessageType").getAsString());

        switch (serverMessageType) {
            case NOTIFICATION -> handleNotification(new Gson().fromJson(messageObject, NotificationMessage.class));
            case ERROR -> handleError(new Gson().fromJson(messageObject, ErrorMessage.class));
            case LOAD_GAME -> handleLoadGame(new Gson().fromJson(messageObject, LoadGameMessage.class));
        }
    }

    public void connect(String authToken, int gameID) throws ClientException {
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
        String notifyMessage =  EscapeSequences.SET_TEXT_COLOR_RED +
                                "Error: " +
                                message.getMessage() +
                                EscapeSequences.RESET_TEXT_COLOR;
        notificationHandler.notify(new NotificationMessage(notifyMessage));
    }

    private void handleLoadGame(LoadGameMessage message) {
        client.loadGame(message.getGame());
    }
}
