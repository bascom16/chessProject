package server.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;


public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        connections.put(username, new Connection(username, session));
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludeName, ServerMessage message) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<>();
        for (Connection connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (!connection.username.equals(excludeName)) {
                    connection.send(message.toString());
                }
            } else {
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
            throw new IOException("User not found");
        }
        connection.send(message.toString());
    }
 }
