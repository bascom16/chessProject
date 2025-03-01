package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO extends DAO<UserData>{
    @Override
    void create(UserData userData);

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
