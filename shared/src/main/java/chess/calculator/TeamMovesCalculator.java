package chess.calculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class TeamMovesCalculator {
    private final ChessBoard board;

    public TeamMovesCalculator(ChessBoard board) {
        this.board = board;
    }

    public Collection<ChessMove> getAllTeamMoves(ChessGame.TeamColor teamColor) {
        return getTeamMovesByLevel(0, teamColor);
    }

    public Collection<ChessMove> getValidTeamMoves(ChessGame.TeamColor teamColor) {
        return getTeamMovesByLevel(1, teamColor);
    }

    private Collection<ChessMove> getTeamMovesByLevel(int level, ChessGame.TeamColor teamColor) {
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
            Collection<ChessMove> pieceMoveList =
                    pieceList.get(index).pieceMovesByLevel(level, board, positionList.get(index));
            teamMoveList.addAll(pieceMoveList);
        }
        return teamMoveList;
    }
}
