package chess.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

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
        for (int iVertical : singleShift) {
            for (int jVertical : doubleShift) {
                ChessPosition endPosition = new ChessPosition(myRow + iVertical, myCol + jVertical);
                ChessMove nextMove = new ChessMove(position, endPosition, null);
                if (checkMove(board, nextMove)) {
                    moveList.add(nextMove);
                }
            }
        }
        for (int iHorizontal : doubleShift) {
            for (int jHorizontal : singleShift) {
                ChessPosition endPosition = new ChessPosition(myRow + iHorizontal, myCol + jHorizontal);
                ChessMove nextMove = new ChessMove(position, endPosition, null);
                if (checkMove(board, nextMove)) {
                    moveList.add(nextMove);
                }
            }
        }
        return moveList;
    }
}
