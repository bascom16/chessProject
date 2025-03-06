package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authDataMap = new HashMap<>() {
    };

    @Override
    public void create(AuthData authData) throws DataAccessException {
        if (authData == null) {
            throw new DataAccessException("Passed in null value");
        }
        String authToken = authData.authToken();
        authDataMap.put(authToken, authData);
    }

    @Override
    public AuthData read(String authData) {
        return authDataMap.get(authData);
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
        String authToken = authData.authToken();
        if (!isInDatabase(authToken)) {
            throw new DataAccessException("Deletion failed: Authorization does not exist");
        }
        authDataMap.remove(authToken);
    }

    @Override
    public void deleteAll() {
        authDataMap.clear();
    }

    private Boolean isInDatabase(String authToken) {
        return authDataMap.containsKey(authToken);
    }
}
