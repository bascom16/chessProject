package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO extends DAO<UserData, String>{
    @Override
    void create(UserData userData) throws DataAccessException;

    @Override
    UserData read(String username);

    @Override
    Collection<UserData> readAll();

    @Override
    void update(UserData userData) throws DataAccessException;

    @Override
    void delete(UserData userData) throws DataAccessException;

    @Override
    void deleteAll();
}
