package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService extends BaseService{

    public ClearService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }

    public void clear() throws DataAccessException {
        if (userDataAccess == null || authDataAccess == null || gameDataAccess == null) {
            throw new DataAccessException("Error: Invalid Data Access");
        }
        userDataAccess.deleteAll();
        authDataAccess.deleteAll();
        gameDataAccess.deleteAll();
    }
}
