package chess;

import chess.calculator.*;

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
    private Boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        hasMoved = false;
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
        allMoves.addAll(castleMoves(board, myPosition));
        allMoves.addAll(enPassantMoves(board, myPosition));
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

    private Collection<ChessMove> castleMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> castleMoveList = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        /* Piece is a king, is not in check, and has not moved. Eligible for Castle. */
        if (myPiece.getPieceType() != PieceType.KING) {
            return castleMoveList;
        } else {
            if (myPosition.getColumn() != 5) {
                myPiece.setHasMoved(true);
            }
        }
        if (myPiece.getHasMoved()) {
            return castleMoveList;
        }
        CheckCalculator checkCalculator = new CheckCalculator(board, myPiece.pieceColor);
        if (checkCalculator.isInCheck()) {
            return castleMoveList;
        }
        int rookRow = myPiece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : 8;
        ChessPosition leftRookPosition = new ChessPosition(rookRow, 1);
        ChessPiece leftRook = board.getPiece(leftRookPosition);
        if (leftRook != null && !leftRook.getHasMoved()) {
            /* left rook is eligible for castle */
            boolean leftEligible = true;
            /* Ensure each tile is empty */
            for (int tileInBetween = 2; tileInBetween <= 4 && leftEligible; tileInBetween++) {
                ChessPosition inBetweenPosition = new ChessPosition(rookRow, tileInBetween);
                if (board.getPiece(inBetweenPosition) != null) {
                    leftEligible = false;
                }
            }
            /* Ensure the king does not pass through check */
            ChessPosition firstPosition = new ChessPosition(rookRow, 4);
            ChessMove firstMove = new ChessMove(myPosition, firstPosition, null);
            if (checkCalculator.moveCausesCheck(firstMove)) {
                leftEligible = false;
            }
            ChessPosition secondPosition = new ChessPosition(rookRow, 3);
            ChessMove secondMove = new ChessMove(myPosition, secondPosition, null);
            if (checkCalculator.moveCausesCheck(secondMove)) {
                leftEligible = false;
            }
            /* if all conditions are met, add left castle to the move list */
            if (leftEligible) {
                castleMoveList.add(secondMove);
            }
        }
        ChessPosition rightRookPosition = new ChessPosition(rookRow, 8);
        ChessPiece rightRook = board.getPiece(rightRookPosition);
        if (rightRook != null && !rightRook.getHasMoved()) {
            /* right rook is eligible for castle */
            boolean rightEligible = true;
            /* Ensure each tile is empty */
            for (int tileInBetween = 7; tileInBetween >= 6 && rightEligible; tileInBetween--) {
                ChessPosition inBetweenPosition = new ChessPosition(rookRow, tileInBetween);
                if (board.getPiece(inBetweenPosition) != null) {
                    rightEligible = false;
                }
            }
            /* Ensure the king does not pass through check */
            ChessPosition firstPosition = new ChessPosition(rookRow, 6);
            ChessMove firstMove = new ChessMove(myPosition, firstPosition, null);
            if (checkCalculator.moveCausesCheck(firstMove)) {
                rightEligible = false;
            }
            ChessPosition secondPosition = new ChessPosition(rookRow, 7);
            ChessMove secondMove = new ChessMove(myPosition, secondPosition, null);
            if (checkCalculator.moveCausesCheck(secondMove)) {
                rightEligible = false;
            }
            /* if all conditions are met, add right castle to the move list */
            if (rightEligible) {
                castleMoveList.add(secondMove);
            }
        }
        return castleMoveList;
    }

    private Collection<ChessMove> enPassantMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> enPassantMoveList = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        /* Current piece must be a pawn and in row 4 or 5 with corresponding team */
        if (myPiece.getPieceType() != PieceType.PAWN) {
            return enPassantMoveList;
        }
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        int direction = myPiece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        if (myRow == 4 && direction == -1 || myRow == 5 && direction == 1) {
            /* Previous move must be by a pawn, distance of 2, and in a neighboring column */
            ChessPosition previousMovePosition = board.getPreviousMove().getEndPosition();
            ChessPiece previousPiece = board.getPiece(previousMovePosition);
            if (previousPiece.getPieceType() != PieceType.PAWN) {
                return enPassantMoveList;
            }
            int previousMoveDistance = Math.abs(board.getPreviousMove().getStartPosition().getRow() - previousMovePosition.getRow());
            if (previousMoveDistance != 2) {
                return enPassantMoveList;
            }
            int horizontalDirection = myCol - previousMovePosition.getColumn();
            if (horizontalDirection == 1 || horizontalDirection == -1) {
                /* Eligible for En Passant */
                ChessPosition enPassantEndPosition = new ChessPosition(myRow + direction, myCol - horizontalDirection);
                ChessMove enPassantMove = new ChessMove(myPosition, enPassantEndPosition, null);
                enPassantMoveList.add(enPassantMove);
            }
        }
        return enPassantMoveList;
    }

    public Boolean getHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(Boolean hasMoved) {
        this.hasMoved = hasMoved;
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

    @Override
    public ChessPiece clone() {
        try {
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
