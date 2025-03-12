package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import com.google.gson.Gson;

public class MySQLGameDAO extends MySQLDAO implements GameDAO {
    public MySQLGameDAO() throws DataAccessException {
        super();
    }

    @Override
    public void create(GameData gameData) throws DataAccessException {
        if (gameData == null) {
            throw new DataAccessException("Unable to create game: invalid input");
        }
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement =  "INSERT INTO game (gameID, whiteUser, blackUser, gameName, gameData)" +
                                " VALUES (?, ?, ?, ?, ?);";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, getGameID());
                preparedStatement.setString(2, gameData.whiteUsername());
                preparedStatement.setString(3, gameData.blackUsername());
                preparedStatement.setString(4, gameData.gameName());
                Object gameJSON = new Gson().toJson(gameData.game());
                preparedStatement.setString(5, gameJSON.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to create user");
        }
    }

    @Override
    public GameData read(Integer gameID) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUser, blackUser, gameName, gameData FROM game WHERE gameID=?;";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return readGameData(resultSet);
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to read game");
        }
        return null;
    }

    private GameData readGameData(ResultSet resultSet) throws SQLException {
        int gameID = resultSet.getInt("gameID");
        String whiteUsername = resultSet.getString("whiteUser");
        String blackUsername = resultSet.getString("blackUser");
        String gameName = resultSet.getString("gameName");
        String gameDataObject = resultSet.getString("gameData");
        ChessGame gameData = new Gson().fromJson(gameDataObject, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameData);
    }

    @Override
    public Collection<GameData> readAll() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void update(GameData gameData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void delete(GameData gameData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteAll() throws DataAccessException {
        String statement = "TRUNCATE TABLE game;";
        executeBasicStatement(statement, "Unable to delete user data");
    }

    static int gameID = 0;

    @Override
    public int getGameID() {
        gameID += 1;
        return gameID;
    }

    @Override
    public void reset() throws DataAccessException {
        String statement = "DROP TABLE game;";
        executeBasicStatement(statement, "Unable to reset game table");
        configureDatabase();
        gameID = 0;
    }
}
