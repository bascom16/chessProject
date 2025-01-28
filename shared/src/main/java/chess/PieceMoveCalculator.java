package chess;

import java.util.Collection;

public interface PieceMoveCalculator {
    Collection<ChessMove> pieceMove (ChessBoard board, ChessPosition position);

    default boolean checkMove(ChessBoard board, ChessMove move) {
        ChessPosition endPosition = move.getEndPosition();
        int endRow = endPosition.getRow();
        if (endRow < 1 || endRow > 8) {
            return false;
        }
        int endCol = endPosition.getColumn();
        if (endCol < 1 || endCol > 8) {
            return false;
        }
        // Check if end space is occupied
        if (!isOccupied(board, endPosition)) {
            return true;
        }
        ChessGame.TeamColor otherColor = board.getPiece(endPosition).getTeamColor();
        ChessGame.TeamColor myColor = board.getPiece(move.getStartPosition()).getTeamColor();
        return otherColor != myColor;
    }

    default boolean isOccupied(ChessBoard board, ChessPosition position) {
        return board.getPiece(position) != null;
    }
}
