package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public ChessPiece clone() {
        try {
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> new KingMoveCalculator().pieceMove(board, myPosition);
            case QUEEN -> new QueenMoveCalculator().pieceMove(board, myPosition);
            case BISHOP -> new BishopMoveCalculator().pieceMove(board, myPosition);
            case KNIGHT -> new KnightMoveCalculator().pieceMove(board, myPosition);
            case ROOK -> new RookMoveCalculator().pieceMove(board, myPosition);
            case PAWN -> new PawnMoveCalculator().pieceMove(board, myPosition);
        };
    }

    public Collection<ChessMove> validPieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> allMoves = pieceMoves(board, myPosition);
        Collection<ChessMove> invalidMoves = new ArrayList<>();
        for (ChessMove move : allMoves) {
            CheckCalculator checkCalculator = new CheckCalculator(board, pieceColor);
            if (checkCalculator.moveCausesCheck(move)) {
                invalidMoves.add(move);
            }
        }
        allMoves.removeAll(invalidMoves);
        return allMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
