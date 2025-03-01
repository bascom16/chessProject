package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO extends DAO<AuthData>{
    @Override
    void create(AuthData authData);

    @Override
    AuthData read(String username);

    @Override
    Collection<AuthData> readAll();

    @Override
    void update (AuthData authData) throws DataAccessException;

    @Override
    void delete(AuthData authData) throws DataAccessException;

    @Override
    void deleteAll();
}
