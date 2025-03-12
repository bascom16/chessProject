package dataaccess;

import java.util.Collection;

public interface DAO<T, K> {
    void create(T data) throws DataAccessException;

    T read(K identifier) throws DataAccessException;

    Collection<T> readAll() throws DataAccessException;

    void update(T data) throws DataAccessException;

    void delete(T data) throws DataAccessException;

    void deleteAll() throws DataAccessException;

    void reset() throws DataAccessException;
}
