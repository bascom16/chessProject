package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.*;
import handler.request.*;
import handler.result.*;

public class ServiceManager {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public ServiceManager(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public RegisterResult register(RegisterRequest request) {
        RegisterService service = new RegisterService(userDataAccess, authDataAccess, gameDataAccess);

        throw new RuntimeException("Not implemented");
    }
}
