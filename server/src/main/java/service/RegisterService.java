package service;

import dataaccess.*;


public class RegisterService extends ServiceManager {
    public RegisterService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }
}
