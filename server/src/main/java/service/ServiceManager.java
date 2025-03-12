package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import handler.request.*;
import handler.result.*;
import model.AuthData;

public class ServiceManager {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;

    public ServiceManager(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException, DataAccessException {
        RegisterService service = new RegisterService(userDataAccess, authDataAccess, gameDataAccess);

        String username = request.username();
        String password = request.password();
        String email = request.email();
        AuthData authData = service.register(username, password, email);

        return new RegisterResult(username, authData.authToken());
    }

    public void clear() throws DataAccessException {
        ClearService service = new ClearService(userDataAccess, authDataAccess, gameDataAccess);
        service.clear();
    }

    public LoginResult login(LoginRequest request) throws ResponseException, DataAccessException {
        LoginService service = new LoginService(userDataAccess, authDataAccess, gameDataAccess);
        String username = request.username();
        String password = request.password();
        AuthData authData = service.login(username, password);

        return new LoginResult(username, authData.authToken());
    }

    public void logout(AuthorizationRequest request) throws ResponseException, DataAccessException {
        LogoutService service = new LogoutService(userDataAccess, authDataAccess, gameDataAccess);
        String authToken = request.authToken();
        service.logout(authToken);
    }

    public ListResult list(AuthorizationRequest request) throws ResponseException, DataAccessException {
        ListService service = new ListService(userDataAccess, authDataAccess, gameDataAccess);
        String authToken = request.authToken();
        return new ListResult(service.list(authToken));
    }

    public CreateResult create(AuthorizationRequest authRequest, CreateRequest createRequest)
            throws ResponseException, DataAccessException {
        CreateService service = new CreateService(userDataAccess, authDataAccess, gameDataAccess);
        return service.create(authRequest.authToken(), createRequest.gameName());
    }

    public void join(AuthorizationRequest authRequest, JoinRequest joinRequest)
            throws ResponseException, DataAccessException {
        JoinService service = new JoinService(userDataAccess, authDataAccess, gameDataAccess);
        service.join(authRequest.authToken(), joinRequest.playerColor(), joinRequest.gameID());
    }
}
