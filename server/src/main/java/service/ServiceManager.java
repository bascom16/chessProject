package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
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

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        RegisterService service = new RegisterService(userDataAccess, authDataAccess, gameDataAccess);

        String username = request.username();
        String password = request.password();
        String email = request.email();
        String authToken = service.register(username, password, email);

        return new RegisterResult(username, authToken);
    }

    public void clear() throws DataAccessException {
        ClearService service = new ClearService(userDataAccess, authDataAccess, gameDataAccess);
        service.clear();
    }

    public LoginResult login() throws ResponseException {
        throw new RuntimeException("Not implemented");
    }
}
