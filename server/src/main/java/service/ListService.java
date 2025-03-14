package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public class ListService extends BaseService {
    public ListService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }

    public Collection<GameData> list(String authToken) throws ResponseException, DataAccessException {
        authenticate(authToken);
        return listGameData();
    }

    private Collection<GameData> listGameData() throws DataAccessException {
        return gameDataAccess.readAll();
    }
}
