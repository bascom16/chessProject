package chess.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMove(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moveList = new ArrayList<>();
        int myRow = position.getRow();
        int myCol = position.getColumn();
        int[] shiftDirection = {1, -1};
        for (int i : shiftDirection) {
            int endRow = myRow + i;
            while (true) {
                ChessPosition endPosition = new ChessPosition(endRow, myCol);
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
            }
        }
        for (int j : shiftDirection) {
            int endCol = myCol + j;
            while (true) {
                ChessPosition endPosition = new ChessPosition(myRow, endCol);
                ChessMove nextMove = new ChessMove(position, endPosition, null);
                if (checkMove(board, nextMove)) {
                    moveList.add(nextMove);
                    if (isOccupied(board, endPosition)) {
                        break;
                    }
                } else {
                    break;
                }
                endCol += j;
            }
        }
        return moveList;
    }
}
