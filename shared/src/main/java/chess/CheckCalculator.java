package chess;

import java.util.Collection;

public class CheckCalculator {
    private final ChessBoard board;
    private final ChessGame.TeamColor teamColor;

    CheckCalculator(ChessBoard board, ChessGame.TeamColor teamColor) {
        this.board = board;
        this.teamColor = teamColor;
    }

    public Boolean isInCheck() {
        /* Calculate all valid moves for the opposite team. */
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

    private ChessPosition getKingPosition() {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                if (board.getPiece(currentPosition) != null) {
                    if (board.getPiece(currentPosition).getPieceType() == ChessPiece.PieceType.KING) {
                        return currentPosition;
                    }
                }
            }
        }
        return null;
    }

    private ChessGame.TeamColor getOpponentTeamColor() {
        return teamColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
    }

    public Boolean moveCausesCheck(ChessMove move) {
        ChessBoard copyBoard = board.clone();
        copyBoard.makeMove(move);
        return new CheckCalculator(copyBoard, teamColor).isInCheck();
    }
}
