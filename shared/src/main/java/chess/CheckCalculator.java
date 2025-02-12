package chess;

import chess.calculator.TeamMovesCalculator;

import java.util.Collection;

public class CheckCalculator {
    protected final ChessBoard board;
    protected final ChessGame.TeamColor teamColor;

    CheckCalculator(ChessBoard board, ChessGame.TeamColor teamColor) {
        this.board = board;
        this.teamColor = teamColor;
    }

    public Boolean isInCheck() {
        /* Calculate all moves for the opposite team. */
        ChessGame.TeamColor oppositeColor = getOpponentTeamColor();
        Collection<ChessMove> teamMoves = new TeamMovesCalculator(board).getTeamMoves(oppositeColor);
        /* See if any of those moves end on the King's position. */
        ChessPosition kingPosition = getKingPosition();
        for (ChessMove currentMove : teamMoves) {
            if (currentMove.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    protected ChessPosition getKingPosition() {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                if (board.getPiece(currentPosition) != null) {
                    ChessPiece currentPiece = board.getPiece(currentPosition);
                    if (currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == teamColor) {
                        return currentPosition;
                    }
                }
            }
        }
        return null;
    }

    protected ChessGame.TeamColor getOpponentTeamColor() {
        return teamColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
    }

    protected Boolean moveCausesCheck(ChessMove move) {
        ChessBoard copyBoard = board.clone();
        copyBoard.makeMove(move);
        CheckCalculator checkCalculator = new CheckCalculator(copyBoard, teamColor);
        return checkCalculator.isInCheck();
    }
}
