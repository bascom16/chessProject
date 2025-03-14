package server;

import model.AuthData;
import model.GameData;

import java.util.Collection;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

//    Returns username and authToken
    public void register(String username, String password, String email) {
        throw new RuntimeException("Not implemented");
    }

//    Returns username and authToken
    public void login(String username, String password) {
        throw new RuntimeException("Not implemented");
    }

    public void logout(String authToken) {
        throw new RuntimeException("Not implemented");
    }

//    Returns list of gameData
    public Collection<GameData> list(String authToken) {
        throw new RuntimeException("Not implemented");
    }

//    Returns gameID
    public Integer create(String gameName, String authToken) {
        throw new RuntimeException("Not implemented");
    }

    public void join(String playerColor, Integer gameID) {
        throw new RuntimeException("Not implemented");
    }
}
