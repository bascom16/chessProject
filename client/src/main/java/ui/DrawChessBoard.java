package ui;

import chess.*;
import state.GameplayState;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DrawChessBoard {

//    Colors
    private static final String SET_BORDER_COLOR = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String SET_BORDER_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
    private static final String SET_LIGHT_TILE_COLOR = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String SET_LIGHT_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
    private static final String SET_DARK_TILE_COLOR = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
    private static final String SET_DARK_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_DARK_GREEN;
    private static final String SET_WHITE_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String SET_BLACK_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_BLACK;

    private static final String SET_HIGHLIGHT_START_TILE_COLOR = EscapeSequences.SET_BG_COLOR_YELLOW;
    private static final String SET_HIGHLIGHT_START_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_DARK_BLUE;

    private static final String SET_HIGHLIGHT_LIGHT_TILE_COLOR = EscapeSequences.SET_BG_COLOR_BLUE;
    private static final String SET_HIGHLIGHT_LIGHT_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private static final String SET_HIGHLIGHT_DARK_TILE_COLOR = EscapeSequences.SET_BG_COLOR_DARK_BLUE;
    private static final String SET_HIGHLIGHT_DARK_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_DARK_BLUE;
    private static final String SET_HIGHLIGHT_VALID_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_YELLOW;

    private static final int A = 1;
    private static final int H = 8;

    private static int startRow;
    private static int endRow;
    private static int startCol;
    private static int endCol;

    private static ChessBoard drawBoard;
    private static PrintStream out;
    private static GameplayState gameplayState;

    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.ERASE_SCREEN);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        out.println();
        drawBoard(board, out, GameplayState.WHITE);
        out.println();
        drawBoard(board, out, GameplayState.BLACK);
        out.println();
        drawBoard(board, out, GameplayState.OBSERVE);
        out.println();

        ChessGame game = new ChessGame();
        try {
            drawHighlightedBoard(game, out, GameplayState.WHITE, new ChessPosition(2, 3));
            game.makeMove(new ChessMove(new ChessPosition(2, 3), new ChessPosition(3, 3), null));
            drawHighlightedBoard(game, out, GameplayState.BLACK, new ChessPosition(7, 8));
            game.makeMove(new ChessMove(new ChessPosition(7, 8), new ChessPosition(6, 8), null));
            drawHighlightedBoard(game, out, GameplayState.WHITE, new ChessPosition(1, 4));
            game.makeMove(new ChessMove(new ChessPosition(1, 4), new ChessPosition(3, 2), null));
            drawHighlightedBoard(game, out, GameplayState.WHITE, new ChessPosition(3, 2));
        } catch (Exception ex) {
            out.print("Invalid move " + ex.getMessage());
        }
    }

    public static void drawBoard(ChessBoard board, PrintStream stream, GameplayState state) {
        initialize(board, stream, state);
        drawBorder();
        drawMiddle();
        drawBorder();
    }

    private static void initialize(ChessBoard board, PrintStream stream, GameplayState state) {
        gameplayState = state;
        drawBoard = board;
        out = stream;
        if (gameplayState == GameplayState.BLACK || gameplayState == GameplayState.BOTH) {
            startRow = 1;
            endRow = 8;
            startCol = H;
            endCol = A;
        } else {
            startRow = 8;
            endRow = 1;
            startCol = A;
            endCol = H;
        }
    }

//    border function
    private static void drawBorder() {
        out.print("   ");
        out.print(SET_BORDER_COLOR);
        out.print(SET_BORDER_TEXT_COLOR);
        out.print("  ");

        List<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"));
        if (gameplayState == GameplayState.BLACK || gameplayState == GameplayState.BOTH) {
            Collections.reverse(letters);
        }

        for (String letter : letters) {
            out.print("  ");
            out.print(letter);
        }
        out.print("    ");
        out.print(EscapeSequences.RESET_BG_COLOR);
        out.println();
    }

//    middle function
    private static void drawMiddle() {
        int shift = startRow > endRow ? -1 : 1;
        // iterate through each row
        for (int i = startRow; i != endRow + shift; i+= shift) {
            out.print("   ");
            drawRow(i);
        }
    }

//    row function
    private static void drawRow(int row) {
        out.print(SET_BORDER_COLOR);
        out.print(SET_BORDER_TEXT_COLOR);
        out.print(" ");
        out.print(row);
        out.print(" ");

        int shift = startCol > endCol ? -1 : 1;
        for (int j = startCol; j != endCol + shift; j += shift) {
            out.print(getTileColor(row, j));
            drawPiece(row, j);
        }

        out.print(SET_BORDER_COLOR);
        out.print(SET_BORDER_TEXT_COLOR);
        out.print(" ");
        out.print(row);
        out.print(" ");
        out.print(EscapeSequences.RESET_BG_COLOR);
        out.println();
    }

    private static String getTileColor(int row, int col) {
        return ((row + col) % 2) == 0 ? SET_DARK_TILE_COLOR : SET_LIGHT_TILE_COLOR;
    }

//    draw piece & blank
    private static void drawPiece(int row, int col) {
        ChessPiece piece = drawBoard.getPiece(new ChessPosition(row, col));

        if (piece == null) {
            out.print(getBlankSpace(row, col));
        } else {
            ChessGame.TeamColor pieceColor = piece.getTeamColor();
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                out.print(SET_WHITE_PIECE_COLOR);
            } else {
                out.print(SET_BLACK_PIECE_COLOR);
            }

            ChessPiece.PieceType pieceType = piece.getPieceType();
            if (pieceType == null) {
                out.print(getBlankSpace(row, col));
            } else {
                switch (pieceType) {
                    case PAWN -> out.print(EscapeSequences.BLACK_PAWN);
                    case ROOK -> out.print(EscapeSequences.BLACK_ROOK);
                    case KNIGHT -> out.print(EscapeSequences.BLACK_KNIGHT);
                    case BISHOP -> out.print(EscapeSequences.BLACK_BISHOP);
                    case QUEEN -> out.print(EscapeSequences.BLACK_QUEEN);
                    case KING -> out.print(EscapeSequences.BLACK_KING);
                }
            }
        }
    }

    private static String getBlankSpace(int row, int col) {
        String backgroundColor = getTileColor(row, col);
        String textColor = Objects.equals(backgroundColor, SET_LIGHT_TILE_COLOR)
                ? SET_LIGHT_TEXT_COLOR : SET_DARK_TEXT_COLOR;
        return textColor + EscapeSequences.WHITE_PAWN;
    }

    public static void drawHighlightedBoard(ChessGame game,
                                            PrintStream stream,
                                            GameplayState state,
                                            ChessPosition position) {
        ArrayList<ChessMove> validMoves = (ArrayList<ChessMove>) game.validMoves(position);
        ArrayList<ChessPosition> validPositions = new ArrayList<>();
        for (ChessMove move : validMoves) {
            validPositions.add(move.getEndPosition());
        }
        initialize(game.getBoard(), stream, state);
        drawBorder();
        drawHighlightedMiddle(position, validPositions);
        drawBorder();
    }

    private static void drawHighlightedMiddle(ChessPosition startPosition, ArrayList<ChessPosition> validPositions) {
        int shift = startRow > endRow ? -1 : 1;
        // iterate through each row
        for (int i = startRow; i != endRow + shift; i+= shift) {
            out.print("   ");
            drawHighlightedRow(i, startPosition, validPositions);
        }
    }

    private static void drawHighlightedRow(int row, ChessPosition startPosition, ArrayList<ChessPosition> validPositions) {
        out.print(SET_BORDER_COLOR);
        out.print(SET_BORDER_TEXT_COLOR);
        out.print(" ");
        out.print(row);
        out.print(" ");

        int shift = startCol > endCol ? -1 : 1;
        for (int j = startCol; j != endCol + shift; j += shift) {
            out.print(getHighlightedTileColor(row, j, startPosition, validPositions));
            out.print(getHighlightedPiece(row, j, startPosition, validPositions));
        }

        out.print(SET_BORDER_COLOR);
        out.print(SET_BORDER_TEXT_COLOR);
        out.print(" ");
        out.print(row);
        out.print(" ");
        out.print(EscapeSequences.RESET_BG_COLOR);
        out.println();
    }

    private static String getHighlightedTileColor(int row,
                                                  int col,
                                                  ChessPosition startPosition,
                                                  ArrayList<ChessPosition> validPositions) {
        ChessPosition currentPosition = new ChessPosition(row, col);
        if (currentPosition.equals(startPosition)) {
            return SET_HIGHLIGHT_START_TILE_COLOR;
        }
        if (validPositions.contains(currentPosition)) {
            return ((row + col) % 2) == 0 ? SET_HIGHLIGHT_DARK_TILE_COLOR : SET_HIGHLIGHT_LIGHT_TILE_COLOR;
        }
        return getTileColor(row, col);
    }

    private static String getHighlightedPiece(int row,
                                              int col,
                                              ChessPosition startPosition,
                                              ArrayList<ChessPosition> validPositions) {
        StringBuilder builder = new StringBuilder();
        ChessPiece piece = drawBoard.getPiece(new ChessPosition(row, col));
        if (piece == null) { // no piece
            builder.append(getHighlightBlankSpace(row, col, startPosition, validPositions));
        } else { // piece exists
            ChessPosition position = new ChessPosition(row, col);
            ChessGame.TeamColor pieceColor = piece.getTeamColor();
            if (position.equals(startPosition)) { // start piece
                builder.append(SET_HIGHLIGHT_START_PIECE_COLOR);
            } else { // piece exists but is not start piece
                if (validPositions.contains(position)) {
                    builder.append(SET_HIGHLIGHT_VALID_PIECE_COLOR);
                } else {
                    if (pieceColor == ChessGame.TeamColor.WHITE) {
                        builder.append(SET_WHITE_PIECE_COLOR);
                    } else {
                        builder.append(SET_BLACK_PIECE_COLOR);
                    }
                }
            }
            String pieceCharacter = switch (piece.getPieceType()) {
                case PAWN -> EscapeSequences.BLACK_PAWN;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case KING -> EscapeSequences.BLACK_KING;
            };
            builder.append(pieceCharacter);
        }
        return builder.toString();
    }

    private static String getHighlightBlankSpace(int row,
                                                 int col,
                                                 ChessPosition startPosition,
                                                 ArrayList<ChessPosition> validPositions) {
        String backgroundColor = getHighlightedTileColor(row, col, startPosition, validPositions);
        String textColor = "";
        if (backgroundColor.contains(SET_LIGHT_TILE_COLOR)) {
            textColor = SET_LIGHT_TEXT_COLOR;
        } else if (backgroundColor.contains(SET_DARK_TILE_COLOR)) {
            textColor = SET_DARK_TEXT_COLOR;
        } else if (backgroundColor.contains(SET_HIGHLIGHT_LIGHT_TILE_COLOR)) {
            textColor = SET_HIGHLIGHT_LIGHT_TEXT_COLOR;
        } else if (backgroundColor.contains(SET_HIGHLIGHT_DARK_TILE_COLOR)) {
            textColor = SET_HIGHLIGHT_DARK_TEXT_COLOR;
        }
        return textColor + EscapeSequences.WHITE_PAWN;
    }
}
