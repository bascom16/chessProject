package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class TeamMovesCalculator {
    private final ChessBoard board;

    TeamMovesCalculator (ChessBoard board) {
        this.board = board;
    }

    public Collection<ChessMove> getTeamMoves(ChessGame.TeamColor teamColor) {
        ArrayList<ChessMove> teamMoveList = new ArrayList<>();
        ArrayList<ChessPosition> positionList = new ArrayList<>();
        ArrayList<ChessPiece> pieceList = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition nextPosition = new ChessPosition(i, j);
                ChessPiece nextPiece = board.getPiece(nextPosition);
                if (nextPiece != null && nextPiece.getTeamColor() == teamColor) {
                    positionList.add(nextPosition);
                    pieceList.add(nextPiece);
                }
            }
        }
        for (int index = 0; index < positionList.size(); index++) {
            Collection<ChessMove> pieceMoveList = pieceList.get(index).pieceMoves(board, positionList.get(index));
            teamMoveList.addAll(pieceMoveList);
        }
        return teamMoveList;
    }

    public Collection<ChessMove> getValidTeamMoves(ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = getTeamMoves(teamColor);
        Collection<ChessMove> invalidMoves = new ArrayList<>();
        for (ChessMove move : moves) {
            CheckCalculator checkCalculator = new CheckCalculator(board, teamColor);
            if (checkCalculator.moveCausesCheck(move)) {
                invalidMoves.add(move);
            }
        }
        moves.removeAll(invalidMoves);
        return moves;
    }
}
