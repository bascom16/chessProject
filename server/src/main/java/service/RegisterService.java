package service;

import dataaccess.*;
import exception.ResponseException;

public class RegisterService extends BaseService {

    public RegisterService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }
    /*
     * Takes in Register Request Data
     * Returns AuthToken as String
     */
    public String register(String username, String password, String email) throws ResponseException {
        if (doesUserExist(username)) {
            throw new ResponseException(403, "already taken");
        }
        if (username == null || password == null || email == null) {
            throw new ResponseException(400, "bad request");
        }
        createUser(username, password, email);
        return createAuth(username);
    }

    private Boolean doesUserExist(String username) {
        return getUser(username) != null;
    }
}
