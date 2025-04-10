package server.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;


public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    Logger log = Logger.getLogger("serverLogger");

    public void add(String username, Session session, int gameID) {
        connections.put(username, new Connection(username, session, gameID));
        log.info(String.format("Added connection for user %s on game %s", username, gameID));
    }

    public void remove(String username) {
        connections.remove(username);
        log.info(String.format("Removed connection for user %s", username));
    }

    public void broadcast(int gameID, String excludeName, ServerMessage message) {
        log.info(String.format("Broadcasting message [%s]", message));
        ArrayList<Connection> removeList = new ArrayList<>();
        for (Connection connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (!connection.username.equals(excludeName) && connection.gameID == gameID) {
                    try {
                        connection.send(message.toString());
                    } catch (Exception ex) {
                        log.warning(String.format
                                ("Message not sent to user %s", connection.username) + ex.getMessage());
                    }
                }
            } else {
                log.info(String.format("Connection for user %s is not open", connection.username));
                removeList.add(connection);
            }
        }
        for (Connection connection : removeList) {
            remove(connection.username);
        }
    }

    public void sendToUser(String username, ServerMessage message) throws IOException {
        Connection connection = connections.get(username);
        if (connection == null) {
            log.info(String.format("Error: Sending message [%s] to user %s failed", message, username));
            throw new IOException("User not found");
        }
        log.info(String.format("Sending message [%s] to user %s", message, username));
        connection.send(message.toString());
    }
 }
