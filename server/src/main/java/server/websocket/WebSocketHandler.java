package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.*;
import org.eclipse.jetty.websocket.api.Session;

public class WebSocketHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = readCommand(message);
        switch (command.getCommandType()) {
            case CONNECT -> connect(session, (ConnectCommand) command);
            case MAKE_MOVE -> makeMove(session, (MakeMoveCommand) command);
            case LEAVE -> leave(session, (LeaveCommand) command);
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

    private void connect(Session session, ConnectCommand command) {
//        TODO: IMPLEMENT CONNECT
        throw new RuntimeException("not implemented");
    }

    private void makeMove(Session session, MakeMoveCommand command) {
//        TODO: IMPLEMENT MOVE
        throw new RuntimeException("not implemented");
    }

    private void leave(Session session, LeaveCommand command) {
//        TODO: IMPLEMENT LEAVE
        throw new RuntimeException("not implemented");
    }

    private void resign(Session session, ResignCommand command) {
//        TODO: IMPLEMENT RESIGN
        throw new RuntimeException("not implemented");
    }
}
