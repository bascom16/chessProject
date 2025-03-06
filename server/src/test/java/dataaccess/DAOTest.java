package dataaccess;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

abstract class DAOTest<T> {
    protected DAO<T> dataAccessObject;
    protected String identifier;
    protected T data;
    protected String identifier2;
    protected T data2;
    protected String identifier3;
    protected T data3;

    @Test
    void createSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        assertNotNull( dataAccessObject.read(identifier));
    }

    @Test
    void createFailure() {
        assertThrows( DataAccessException.class, () -> dataAccessObject.create(null));
    }

    @Test
    void readSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        assertEquals(data, dataAccessObject.read(identifier));
    }

    @Test
    void readFailure() {
        dataAccessObject.deleteAll();
        assertNull(dataAccessObject.read(identifier));
    }

    @Test
    void readAllSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        assertDoesNotThrow( () -> dataAccessObject.create(data2));
        assertDoesNotThrow( () -> dataAccessObject.create(data3));

        ArrayList<T> expectedList = new ArrayList<>();
        expectedList.add(data);
        expectedList.add(data2);
        expectedList.add(data3);

        for (T entry : expectedList) {
            assertTrue(dataAccessObject.readAll().contains(entry));
        }
    }

    @Test
    void readAllFailure() {
        ArrayList<T> emptyList = new ArrayList<>();
        assertEquals(emptyList, dataAccessObject.readAll());
    }

    @Test
    abstract void updateSuccess();

    @Test
    void updateFailure() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        assertThrows(DataAccessException.class, () -> dataAccessObject.update(data2));
    }

    @Test
    void deleteSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        assertDoesNotThrow( () -> dataAccessObject.delete(data));
    }

    @Test
    void deleteFailure() {
        assertThrows(DataAccessException.class, () -> dataAccessObject.delete(data));
    }

    @Test
    void deleteAllSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        assertDoesNotThrow( () -> dataAccessObject.create(data2));
        assertDoesNotThrow( () -> dataAccessObject.create(data3));

        dataAccessObject.deleteAll();

        assertNull(dataAccessObject.read(identifier));
        assertNull(dataAccessObject.read(identifier2));
        assertNull(dataAccessObject.read(identifier3));
    }
}