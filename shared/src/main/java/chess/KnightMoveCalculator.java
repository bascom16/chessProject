package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMove(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moveList = new ArrayList<>();
        int myRow = position.getRow();
        int myCol = position.getColumn();
        int[] singleShift = {1, -1};
        int[] doubleShift = {2, -2};
        for (int i : singleShift) {
            for (int j : doubleShift) {
                ChessPosition endPosition = new ChessPosition(myRow + i, myCol + j);
                ChessMove nextMove = new ChessMove(position, endPosition, null);
                if (checkMove(board, nextMove)) {
                    moveList.add(nextMove);
                }
            }
        }
        for (int i : doubleShift) {
            for (int j : singleShift) {
                ChessPosition endPosition = new ChessPosition(myRow + i, myCol + j);
                ChessMove nextMove = new ChessMove(position, endPosition, null);
                if (checkMove(board, nextMove)) {
                    moveList.add(nextMove);
                }
            }
        }
        return moveList;
    }
}
