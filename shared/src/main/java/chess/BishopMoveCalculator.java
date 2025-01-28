package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMove(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moveList = new ArrayList<>();
        int myRow = position.getRow();
        int myCol = position.getColumn();
        int[] shiftDirection = {1, -1};
        for (int i : shiftDirection) {
            for (int j : shiftDirection) {
                int endRow = myRow + i;
                int endCol = myCol + j;
                while (true) {
                    ChessPosition endPosition = new ChessPosition(endRow, endCol);
                    ChessMove nextMove = new ChessMove(position, endPosition, null);
                    if (checkMove(board, nextMove)) {
                        moveList.add(nextMove);
                        if (isOccupied(board, endPosition)) {
                            break;
                        }
                    } else {
                        break;
                    }
                    endRow += i;
                    endCol += j;
                }
            }
        }
        return moveList;
    }
}
