package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMove(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moveList = new ArrayList<>();
        int myRow = position.getRow();
        int myCol = position.getColumn();
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        int direction = myColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        int[] shift = {1, 0, -1};
        for (int i : shift) {
            ChessPosition endPosition = new ChessPosition(myRow + direction, myCol + i);
            ChessMove nextMove = new ChessMove(position, endPosition, null);
            if (checkMove(board, nextMove)) {
                if (checkPromotion(board, position)) {
                    for (ChessPiece.PieceType promotionPieceType : ChessPiece.PieceType.values()) {
                        if (    promotionPieceType != ChessPiece.PieceType.KING &&
                                promotionPieceType != ChessPiece.PieceType.PAWN) {
                            ChessMove promotionMove = new ChessMove(position, endPosition, promotionPieceType);
                            moveList.add(promotionMove);
                        }
                    }
                } else {
                    moveList.add(nextMove);
                }
            }
        }
        ChessPosition startingEndPosition = new ChessPosition(myRow + direction + direction, myCol);
        ChessMove startMove = new ChessMove(position, startingEndPosition, null);
        if (checkMove(board, startMove)) {
            moveList.add(startMove);
        }
        return moveList;
    }

    @Override
    public boolean checkMove(ChessBoard board, ChessMove move) {
        ChessPosition endPosition = move.getEndPosition();
        int endRow = endPosition.getRow();
        if (endRow < 1 || endRow > 8) {
            return false;
        }
        int endCol = endPosition.getColumn();
        if (endCol < 1 || endCol > 8) {
            return false;
        }
        ChessPosition startPosition = move.getStartPosition();
        ChessGame.TeamColor myColor = board.getPiece(startPosition).getTeamColor();
        int direction = myColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        int myRow = startPosition.getRow();
        int myCol = startPosition.getColumn();
        // Straight ahead cases
        if (myCol == endCol) {
            if (endRow == myRow + direction) { // straight ahead case
                return !isOccupied(board, endPosition);
            } else if (endRow == myRow + direction + direction) { // starting move case
                if (myRow == 2 || myRow == 7) {
                    ChessPosition inBetweenPosition = new ChessPosition(endRow - direction, endCol);
                    return !isOccupied(board, inBetweenPosition) && !isOccupied(board, endPosition);
                }
            } else {
                return false;
            }
        }
        // Diagonal capture cases
        if (endRow == myRow + direction) {
            if (endCol == myCol + 1 || endCol == myCol - 1) {
                if (isOccupied(board, endPosition)) {
                    return myColor != board.getPiece(endPosition).getTeamColor();
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean checkPromotion(ChessBoard board, ChessPosition position) {
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        int direction = myColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        int myRow = position.getRow();
        return myRow == 7 && direction == 1 || myRow == 2 && direction == -1;
    }
}
