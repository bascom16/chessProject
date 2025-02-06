package chess;

import java.util.Collection;

public class CheckmateCalculator extends CheckCalculator {
    CheckmateCalculator(ChessBoard board, ChessGame.TeamColor teamColor) {
        super(board, teamColor);
    }

    public Boolean isInCheckmate() {
        /* If the King is in check and has no moves that will get him out of check, then checkmate */
        Collection<ChessMove> validTeamMoves = new TeamMovesCalculator(board).getValidTeamMoves(teamColor);
        if (isDefinitelyInCheck()) {
            for (ChessMove move : validTeamMoves) {
                if (!moveDefinitelyCausesCheck(move)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean isDefinitelyInCheck() {
        /* Calculate all valid moves for the opposite team. */
        ChessGame.TeamColor oppositeColor = getOpponentTeamColor();
        Collection<ChessMove> teamMoves = new TeamMovesCalculator(board).getValidTeamMoves(oppositeColor);
        /* See if any of those moves end on the King's position. */
        ChessPosition kingPosition = getKingPosition();
        for (ChessMove currentMove : teamMoves) {
            if (currentMove.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private Boolean moveDefinitelyCausesCheck(ChessMove move) {
        ChessBoard copyBoard = board.clone();
        copyBoard.makeMove(move);
        CheckmateCalculator checkmateCalculator = new CheckmateCalculator(copyBoard, teamColor);
        return checkmateCalculator.isDefinitelyInCheck();
    }
}
