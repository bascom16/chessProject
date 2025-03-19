package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import com.google.gson.Gson;

public class MySQLGameDAO extends MySQLDAO implements GameDAO {
    public MySQLGameDAO() throws DataAccessException {
        super();
        updateGameID();
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
                preparedStatement.setInt(1, gameData.gameID());
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
    public Collection<GameData> readAll() throws DataAccessException {
        Collection<GameData> gameDataList = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUser, blackUser, gameName, gameData FROM game;";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        gameDataList.add(readGameData(resultSet));
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to read games");
        }
        return gameDataList;
    }

    @Override
    public void update(GameData gameData) throws DataAccessException {
        if (!isInDatabase(gameData)) {
            throw new DataAccessException("Unable to update game: game not found");
        }
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement =  "UPDATE game SET whiteUser=?, blackUser=?, gameName=?, gameData=? WHERE gameID=?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                Object gameJSON = new Gson().toJson(gameData.game());
                preparedStatement.setString(4, gameJSON.toString());
                preparedStatement.setInt(5, gameData.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to update user");
        }
    }

    @Override
    public void delete(GameData gameData) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            if (!isInDatabase(gameData)) {
                throw new DataAccessException("Unable to delete game: game not found");
            }
            String statement = "DELETE FROM game WHERE gameID=?;";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DataAccessException("Unable to delete game");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        String statement = "TRUNCATE TABLE game;";
        executeBasicStatement(statement, "Unable to delete user data");
    }

    static int gameID = 0;

    @Override
    public int getGameID() throws DataAccessException {
        updateGameID();
        gameID += 1;
        return gameID;
    }

    private void updateGameID() throws DataAccessException {
        gameID = readAll().size();
    }

    @Override
    public void reset() throws DataAccessException {
        String statement = "DROP TABLE game;";
        executeBasicStatement(statement, "Unable to reset game table");
        configureDatabase();
        gameID = 0;
    }

    private Boolean isInDatabase(GameData data) throws DataAccessException {
        return read(data.gameID()) != null;
    }
}
