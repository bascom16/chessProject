package dataaccess;

import chess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameDAOTest extends DAOTest<GameData, Integer>{
    @BeforeEach
    void setUp() {
        try {
            dataAccessObject = new MySQLGameDAO();
            dataAccessObject.reset();
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
    @Test
    void updateSuccess() {

        assertDoesNotThrow( () -> dataAccessObject.create(data));

        ChessGame complexGame = new ChessGame();
        ChessBoard board = new ChessBoard();
        ChessPosition startPosition = new ChessPosition(1, 1);
        ChessPosition endPosition = new ChessPosition(3, 3);
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board.addPiece(startPosition, piece);
        complexGame.setBoard(board);
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        assertDoesNotThrow( () -> complexGame.makeMove(move));
        GameData modifiedData =
                new GameData(identifier, "user1", "user2", "game1", complexGame);

        assertDoesNotThrow( () -> dataAccessObject.update(modifiedData));
        assertEquals(modifiedData, assertDoesNotThrow( () -> dataAccessObject.read(identifier)));
    }
}
