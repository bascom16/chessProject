package chess;

public class CheckmateCalculator extends CheckCalculator {
    CheckmateCalculator(ChessBoard board, ChessGame.TeamColor teamColor) {
        super(board, teamColor);
    }

    Boolean isInCheckmate() {
        throw new RuntimeException("Not implemented");
    }
}
