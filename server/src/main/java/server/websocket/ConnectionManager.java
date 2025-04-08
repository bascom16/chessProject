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

    public void add(String username, Session session) {
        connections.put(username, new Connection(username, session));
        log.info(String.format("Added connection for user %s", username));
    }

    public void remove(String username) {
        connections.remove(username);
        log.info(String.format("Removed connection for user %s", username));
    }

    public void broadcast(String excludeName, ServerMessage message) throws IOException {
        log.info(String.format("Broadcasting message [%s]", message));
        ArrayList<Connection> removeList = new ArrayList<>();
        for (Connection connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (!connection.username.equals(excludeName)) {
                    connection.send(message.toString());
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
