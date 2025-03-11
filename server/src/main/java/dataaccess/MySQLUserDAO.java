package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.List;

public class MySQLUserDAO extends MySQLDAO implements UserDAO {
    public MySQLUserDAO() throws DataAccessException {
        super();
    }

    @Override
    public void create(UserData userData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public UserData read(String username) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Collection<UserData> readAll() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void update(UserData userData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void delete(UserData userData) throws DataAccessException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException("not implemented");
    }
}
