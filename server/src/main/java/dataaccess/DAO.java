package dataaccess;

import java.util.Collection;

public interface DAO<T> {
    void create(T data) throws DataAccessException;

    T read(String identifier);

    Collection<T> readAll();

    void update(T data) throws DataAccessException;

    void delete(T data) throws DataAccessException;

    void deleteAll();
}
