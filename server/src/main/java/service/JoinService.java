package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.Objects;

public class JoinService extends BaseService {
    public JoinService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }

    public void join(String authToken, String playerColor, Integer gameID)
            throws ResponseException, DataAccessException {
        AuthData authData = authenticate(authToken);
        GameData gameData = gameDataAccess.read(gameID);
        if (gameData == null) {
            throw new ResponseException(400, "bad request");
        }
        if (!isPlayerColorAvailable(playerColor, gameData)) {
            throw new ResponseException(403, "Color already taken");
        }
        GameData newGameData = getGameData(playerColor, authData, gameData);
        gameDataAccess.update(newGameData);
    }

    private static GameData getGameData(String playerColor, AuthData authData, GameData gameData) {
        GameData newGameData;
        String username = authData.username();
        if (Objects.equals(playerColor, "WHITE")) {
            newGameData = new GameData(gameData.gameID(),
                    username,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game());
        } else {
            newGameData = new GameData(gameData.gameID(),
                    gameData.whiteUsername(),
                    username,
                    gameData.gameName(),
                    gameData.game());
        }
        return newGameData;
    }

    private Boolean isPlayerColorAvailable(String playerColor, GameData gameData) {
        if (Objects.equals(playerColor, "WHITE")) {
            return gameData.whiteUsername() == null;
        } else if (Objects.equals(playerColor, "BLACK")) {
            return gameData.blackUsername() == null;
        }
        return false;
    }
}
