package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO extends DAO<AuthData, String>{
    @Override
    void create(AuthData authData) throws DataAccessException;

    @Override
    AuthData read(String authToken);

    @Override
    Collection<AuthData> readAll();

    @Override
    void update (AuthData authData) throws DataAccessException;

    @Override
    void delete(AuthData authData) throws DataAccessException;

    @Override
    void deleteAll();
}
