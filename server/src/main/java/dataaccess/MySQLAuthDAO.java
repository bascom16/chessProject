package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.List;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void create(AuthData authData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AuthData read(String authToken) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Collection<AuthData> readAll() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void update(AuthData authData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void delete(AuthData authData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException("not implemented");
    }
}
