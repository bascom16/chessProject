package dataaccess;

import model.GameData;

import java.util.*;

public class MemoryGameDAO implements GameDAO {
    private Map<Integer, GameData> gameDataMap = new HashMap<>();

    @Override
    public void create(GameData gameData) {
        int gameID = gameData.gameID();
        gameDataMap.put(gameID, gameData);
    }

    @Override
    public GameData read(String gameID) {
        int intID = Integer.parseInt(gameID);
        return read(intID);
    }

    @Override
    public GameData read(Integer gameID) {
        return gameDataMap.get(gameID);
    }

    @Override
    public Collection<GameData> readAll() {
        LinkedList<GameData> gameDataList = new LinkedList<>();
        for (Map.Entry<Integer, GameData> entry : gameDataMap.entrySet()) {
            gameDataList.add(entry.getValue());
        }
        return gameDataList;
    }

    @Override
    public void update(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();
        if (!isInDatabase(gameID)) {
            throw new DataAccessException("Update failed: GameID does not exist");
        }
        gameDataMap.put(gameID, gameData);
    }

    @Override
    public void delete(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();
        if (!isInDatabase(gameID)) {
            throw new DataAccessException("Deletion failed: GameID does not exist");
        }
        gameDataMap.remove(gameID);
    }

    @Override
    public void deleteAll() {
        gameDataMap.clear();
    }

    private Boolean isInDatabase(int gameID) {
        return gameDataMap.containsKey(gameID);
    }

    static int gameID = 0;

    @Override
    public int getGameID() {
        gameID += 1;
        return gameID;
    }
}
