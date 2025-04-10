package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.logging.Logger;

public class Connection {
    public String username;
    public Session session;
    public int gameID;

    Logger log = Logger.getLogger("serverLogger");

    public Connection(String username, Session session, int gameID) {
        this.username = username;
        this.session = session;
        this.gameID = gameID;
    }

    public void send(String msg) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(msg);
        } else {
            log.warning("Attempted to send message to closed session.");
        }
    }
}
