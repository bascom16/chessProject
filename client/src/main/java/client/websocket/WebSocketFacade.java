package client.websocket;

import com.google.gson.Gson;
import exception.ClientException;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
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

    public void makeMove() {
//        TODO: MAKE MOVE
    }

    public void resign() {
//        TODO: RESIGN
    }

    private void sendCommand(UserGameCommand command) throws ClientException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ClientException(500, ex.getMessage());
        }
    }
}
