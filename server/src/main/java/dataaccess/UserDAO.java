package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO extends DAO<UserData, String>{
    @Override
    void create(UserData userData) throws DataAccessException;

    @Override
    UserData read(String username) throws DataAccessException;

    @Override
    Collection<UserData> readAll() throws DataAccessException;

    @Override
    void update(UserData userData) throws DataAccessException;

    @Override
    void delete(UserData userData) throws DataAccessException;

    @Override
    void deleteAll() throws DataAccessException;

    @Override
    void reset() throws DataAccessException;
}
