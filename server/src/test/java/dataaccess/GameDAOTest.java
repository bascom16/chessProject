package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameDAOTest extends DAOTest<GameData, Integer>{
    @BeforeEach
    void setUp() {
        try {
            dataAccessObject = new MySQLGameDAO();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }

        identifier = 1;
        identifier2 = 2;
        identifier3 = 3;
        String user1 = "user1";
        String user2 = "user2";
        ChessGame game = new ChessGame();
        data = new GameData(identifier, user1, user2, "game1", game);
        data2 = new GameData(identifier2, user1, user2, "game2", game);
        data3 = new GameData(identifier3, user1, user2, "game3", game);
    }

    @Override
    void updateSuccess() {

        assertDoesNotThrow( () -> dataAccessObject.create(data));
        GameData modifiedData =
                new GameData(identifier, "user2", "user1", "game4", new ChessGame());
        assertDoesNotThrow( () -> dataAccessObject.update(modifiedData));
        assertEquals(modifiedData, assertDoesNotThrow( () -> dataAccessObject.read(identifier)));
/*
        Edit test to actually update a game!
*/
        throw new RuntimeException("Not implemented");
    }
}
