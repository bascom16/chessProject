package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;

public class LogoutService extends BaseService {
    public LogoutService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }

    public void logout(String authToken) throws ResponseException, DataAccessException {
        AuthData authData = authenticate(authToken);
        deleteAuthData(authData);
    }

    private void deleteAuthData(AuthData authData) throws DataAccessException {
        authDataAccess.delete(authData);
    }
}
