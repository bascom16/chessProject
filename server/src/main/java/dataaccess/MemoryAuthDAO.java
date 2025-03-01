package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private Map<String, AuthData> authDataMap = new HashMap<>() {
    };

    @Override
    public void create(AuthData authData) {
        String username = authData.username();
        authDataMap.put(username, authData);
    }

    @Override
    public AuthData read(String username) {
        return authDataMap.get(username);
    }

    @Override
    public Collection<AuthData> readAll() {
        LinkedList<AuthData> authDataList = new LinkedList<>();
        for (Map.Entry<String, AuthData> entry : authDataMap.entrySet()) {
            authDataList.add(entry.getValue());
        }
        return authDataList;
    }

    @Override
    public void update(AuthData authData) throws DataAccessException {
        String username = authData.username();
        if (!isInDatabase(username)) {
            throw new DataAccessException("Update failed: Authorization does not exist");
        }
        authDataMap.put(username, authData);
    }

    @Override
    public void delete(AuthData authData) throws DataAccessException {
        String username = authData.username();
        if (!isInDatabase(username)) {
            throw new DataAccessException("Deletion failed: Authorization does not exist");
        }
        authDataMap.remove(username);
    }

    @Override
    public void deleteAll() {
        authDataMap.clear();
    }

    private Boolean isInDatabase(String username) {
        return authDataMap.containsKey(username);
    }
}
