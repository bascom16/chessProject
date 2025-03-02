package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO extends DAO<GameData>{
    @Override
    void create(GameData gameData);

    @Override
    GameData read(String gameID);

    GameData read(Integer gameID);

    @Override
    Collection<GameData> readAll();

    @Override
    void update(GameData gameData) throws DataAccessException;

    @Override
    void delete(GameData gameData) throws DataAccessException;

    @Override
    void deleteAll();

    int getGameID();
}
