package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import java.util.UUID;

import exception.ResponseException;
import model.*;

public class BaseService {
    protected final UserDAO userDataAccess;
    protected final AuthDAO authDataAccess;
    protected final GameDAO gameDataAccess;

    public BaseService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public UserData getUser(String username) {
        return userDataAccess.read(username);
    }

    public void createUser(String username, String password, String email) {
        UserData userData = new UserData(username, password, email);
        userDataAccess.create(userData);
    }

    public AuthData createAuth(String username) {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        authDataAccess.create(authData);
        return authData;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    protected AuthData authenticate(String authToken) throws ResponseException {
        AuthData authData = authDataAccess.read(authToken);
        if (authData == null) {
            throw new ResponseException(401, "unauthorized");
        } else {
            return authData;
        }
    }
}
