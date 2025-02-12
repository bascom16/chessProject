package chess;

import chess.moveCalculators.TeamMovesCalculator;

import java.util.Collection;

public class StalemateCalculator {
    private final ChessBoard board;
    private final ChessGame.TeamColor teamColor;

    public StalemateCalculator (ChessBoard board, ChessGame.TeamColor teamColor) {
        this.board = board;
        this.teamColor = teamColor;
    }

    public Boolean isInStalemate () {
        Collection<ChessMove> teamMoves = new TeamMovesCalculator(board).getValidTeamMoves(teamColor);
        Boolean isInCheck = new CheckCalculator(board, teamColor).isInCheck();
        return teamMoves.isEmpty() && !isInCheck;
    }
}
