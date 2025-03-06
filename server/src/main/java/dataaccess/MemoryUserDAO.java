package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> userDataMap = new HashMap<>();

    @Override
    public void create(UserData userData) throws DataAccessException {
        if (userData == null) {
            throw new DataAccessException("Passed in null value");
        }
        String username = userData.username();
        userDataMap.put(username, userData);
    }

    @Override
    public UserData read(String username) {
        return userDataMap.get(username);
    }

    @Override
    public Collection<UserData> readAll() {
        LinkedList<UserData> userDataList = new LinkedList<>();
        for (Map.Entry<String, UserData> entry : userDataMap.entrySet()) {
            userDataList.add(entry.getValue());
        }
        return userDataList;
    }

    @Override
    public void update(UserData userData) throws DataAccessException {
        String username = userData.username();
        if (!isInDatabase(username)) {
            throw new DataAccessException("Update failed: User does not exist");
        }
        userDataMap.put(username, userData);
    }

    @Override
    public void delete(UserData userData) throws DataAccessException {
        String username = userData.username();
        if (!isInDatabase(username)) {
            throw new DataAccessException("Deletion failed: User does not exist");
        }
        userDataMap.remove(username);
    }

    @Override
    public void deleteAll() {
        userDataMap.clear();
    }

    private Boolean isInDatabase(String username) {
        return userDataMap.containsKey(username);
    }
}
