package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    @Override
    public void create(GameData gameData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public GameData read(Integer gameID) {
        throw new RuntimeException("not implemented");
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
    public void deleteAll() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public int getGameID() {
        throw new RuntimeException("not implemented");
    }
}
