package dataaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

import Logger.LoggerManager;

abstract class DAOTest<T, K> {
    protected DAO<T, K> dataAccessObject;
    protected K identifier;
    protected T data;
    protected K identifier2;
    protected T data2;
    protected K identifier3;
    protected T data3;

    @BeforeAll
    static void initLogger() {
        Logger log = Logger.getLogger("testLogger");
        LoggerManager.setup(log, "test.log");
    }


    @Test
    void createSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        assertNotNull( assertDoesNotThrow( () -> dataAccessObject.read(identifier)) );
    }

    @Test
    void createFailure() {
        assertThrows( DataAccessException.class, () -> dataAccessObject.create(null));
    }

    @Test
    void readSuccess() {
        assertDoesNotThrow( () -> dataAccessObject.create(data));
        assertEquals(data, assertDoesNotThrow( () -> dataAccessObject.read(identifier)));
    }

    @Test
    void readFailure() {
        assertDoesNotThrow( () -> dataAccessObject.deleteAll());

        assertNull(assertDoesNotThrow( () -> dataAccessObject.read(identifier)));
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
            assertTrue(assertDoesNotThrow( () -> dataAccessObject.readAll().contains(entry)));
        }
    }

    @Test
    void readAllFailure() {
        ArrayList<T> emptyList = new ArrayList<>();
        assertEquals(emptyList, assertDoesNotThrow( () -> dataAccessObject.readAll()));
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

        assertDoesNotThrow( () -> dataAccessObject.deleteAll());


        assertNull(assertDoesNotThrow( () -> dataAccessObject.read(identifier)));
        assertNull(assertDoesNotThrow( () -> dataAccessObject.read(identifier2)));
        assertNull(assertDoesNotThrow( () -> dataAccessObject.read(identifier3)));
    }

    @AfterEach
    void reset() {
        try {
            dataAccessObject.reset();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException("Table not reset");
        }
    }
}