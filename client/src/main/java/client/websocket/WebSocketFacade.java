package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ClientException;
import websocket.commands.*;
import websocket.messages.NotificationMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    private final Session session;
    private final NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ClientException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    notificationHandler.notify(notification);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ClientException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws ClientException {
        sendCommand(new ConnectCommand(authToken, gameID));
    }

    public void leave(String authToken, int gameID) throws ClientException {
        sendCommand(new LeaveCommand(authToken, gameID));
        try {
            this.session.close();
        } catch (IOException ex) {
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
            throw new ClientException(500, ex.getMessage());
        }
    }
}
