package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO extends DAO<GameData, Integer>{
    @Override
    void create(GameData gameData) throws DataAccessException;

    @Override
    GameData read(Integer gameID) throws DataAccessException;

    @Override
    Collection<GameData> readAll() throws DataAccessException;

    @Override
    void update(GameData gameData) throws DataAccessException;

    @Override
    void delete(GameData gameData) throws DataAccessException;

    @Override
    void deleteAll() throws DataAccessException;

    @Override
    void reset() throws DataAccessException;

    int getGameID() throws DataAccessException;
}
