package chess;

import java.util.Collection;

public class QueenMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMove(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> bishopMoveList = new BishopMoveCalculator().pieceMove(board, position);
        Collection<ChessMove> rookMoveList = new RookMoveCalculator().pieceMove(board, position);
        bishopMoveList.addAll(rookMoveList);
        return bishopMoveList;
    }
}
