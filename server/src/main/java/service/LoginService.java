package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class LoginService extends BaseService {
    public LoginService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }

    public AuthData login(String username, String password) throws ResponseException, DataAccessException {
        UserData userData = getUser(username);
        if (userData == null) {
            throw new ResponseException(401, "unauthorized");
        }
        if (!matchPassword(password, userData)) {
            throw new ResponseException(401, "unauthorized");
        }
        return createAuth(username);
    }

    private Boolean matchPassword(String password, UserData userData) {
        return BCrypt.checkpw(password, userData.password());
    }
}
