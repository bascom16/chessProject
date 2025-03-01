package service;

import dataaccess.*;

import model.UserData;
import model.AuthData;

public class RegisterService extends BaseService {

    public RegisterService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }
    /*
     * Takes in Register Request Data
     * Returns AuthToken as String
     */
    public String register(String username, String password, String email) {
        if (doesUserExist(username)) {
            /* Error. Figure out how to do that. */
            throw new RuntimeException("Not implemented");
        }
        createUser(username, password, email);
        return createAuth(username);
    }

    private Boolean doesUserExist(String username) {
        return getUser(username) != null;
    }
}
