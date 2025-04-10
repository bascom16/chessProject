package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    private Boolean gameOver = false;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    public ChessGame(ChessBoard board) {
        this.board = board;
        this.teamTurn = TeamColor.WHITE;
    }

    public ChessGame(ChessBoard board, TeamColor teamTurn) {
        this.board = board;
        this.teamTurn = teamTurn;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    public void switchTeamTurn() {
        setTeamTurn(teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    public void setGameOver() {
        gameOver = true;
        teamTurn = null;
    }

    public Boolean isGameOver() {
        return gameOver;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);
        return new ArrayList<>(currentPiece.validPieceMoves(board, startPosition));
    }

    public Boolean isValidMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        Collection<ChessMove> validMoveList = validMoves(startPosition);
        return validMoveList.contains(move);
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try {
            ChessPosition startPosition = move.getStartPosition();
            ChessPiece piece = board.getPiece(startPosition);
            if (piece == null) {
                throw new InvalidMoveException("Invalid move. There is no piece at that location.");
            }
            if (piece.getTeamColor() != teamTurn) {
                throw new InvalidMoveException("Invalid move. You can't move the other team's pieces.");
            }
            if (!isValidMove(move)) {
                throw new InvalidMoveException("Invalid move. The move you entered is not possible.");
            }
            ChessBoard copyBoard = board.clone();
            copyBoard.makeMove(move);
            ChessGame copyGame = new ChessGame(copyBoard);
            if (copyGame.isInCheck(teamTurn)) {
                throw new InvalidMoveException("Invalid move. This move would put your king in check.");
            }
            board.makeMove(move);
            switchTeamTurn();
        } catch (Exception ex) {
            if (ex.getClass() == InvalidMoveException.class) {
                throw new InvalidMoveException(ex.getMessage());
            } else {
                System.out.println("Something is very wrong!" + ex.getMessage());
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        CheckCalculator checkCalculator = new CheckCalculator(board, teamColor);
        return checkCalculator.isInCheck();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        CheckmateCalculator checkmateCalculator = new CheckmateCalculator(board, teamColor);
        return checkmateCalculator.isInCheckmate();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return new StalemateCalculator(board, teamColor).isInStalemate();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
