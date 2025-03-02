package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService extends BaseService{

    public ClearService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }

    public void clear() {
        userDataAccess.deleteAll();
        authDataAccess.deleteAll();
        gameDataAccess.deleteAll();
    }
}
