package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import handler.result.CreateResult;
import model.GameData;

public class CreateService extends BaseService{
    public CreateService(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        super(userDataAccess, authDataAccess, gameDataAccess);
    }

    public CreateResult create(String authToken, String gameName) throws ResponseException {
        authenticate(authToken);
        int gameID = gameDataAccess.getGameID();
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDataAccess.create(game);
        return new CreateResult(gameID);
    }
}
