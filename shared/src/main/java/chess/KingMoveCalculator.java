package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMove(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moveList = new ArrayList<>();
        int myRow = position.getRow();
        int myCol = position.getColumn();
        int[] shiftDirection = {1, 0, -1};
        for (int i : shiftDirection) {
            for (int j : shiftDirection) {
                ChessPosition endPosition = new ChessPosition(myRow + i, myCol + j);
                if (!endPosition.equals(position)) {
                    ChessMove nextMove = new ChessMove(position, endPosition, null);
                    if (checkMove(board, nextMove)) {
                        moveList.add(nextMove);
                    }
                }
            }
        }
        return moveList;
    }
}
