package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DrawChessBoard {

//    Colors
    private static final String SET_BORDER_COLOR = EscapeSequences.SET_BG_COLOR_MAGENTA;
    private static final String SET_BORDER_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String SET_LIGHT_TILE_COLOR = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String SET_LIGHT_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
    private static final String SET_DARK_TILE_COLOR = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String SET_DARK_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_DARK_GREY;
    private static final String SET_WHITE_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_YELLOW;
    private static final String SET_BLACK_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_BLUE;

    private static final int A = 1;
    private static final int H = 8;

    private static int startRow;
    private static int endRow;
    private static int startCol;
    private static int endCol;

    private static ChessBoard drawBoard;
    private static ChessGame.TeamColor teamColor;
    private static PrintStream out;

    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.ERASE_SCREEN);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        out.println();
        drawBoard(board, out, ChessGame.TeamColor.WHITE);
        out.println();
        drawBoard(board, out, ChessGame.TeamColor.BLACK);
        out.println();
        drawBoard(board, out, null);
    }

    public static void drawBoard(ChessBoard board, PrintStream stream, ChessGame.TeamColor color) {
        drawBoard = board;
        teamColor = color;
        out = stream;
        if (color == ChessGame.TeamColor.WHITE || color == null) {
            startRow = 8;
            endRow = 1;
            startCol = A;
            endCol = H;
        } else {
            startRow = 1;
            endRow = 8;
            startCol = H;
            endCol = A;
        }
        drawBorder();
        drawMiddle();
        drawBorder();
    }

//    border function
    private static void drawBorder() {
        out.print("   ");
        out.print(SET_BORDER_COLOR);
        out.print(SET_BORDER_TEXT_COLOR);
        out.print("  ");

        List<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"));
        if (teamColor == ChessGame.TeamColor.BLACK) {
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
            drawBlankSpace(row, col);
        } else {
            ChessGame.TeamColor pieceColor = piece.getTeamColor();
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                out.print(SET_WHITE_PIECE_COLOR);
            } else {
                out.print(SET_BLACK_PIECE_COLOR);
            }

            ChessPiece.PieceType pieceType = piece.getPieceType();
            if (pieceType == null) {
                drawBlankSpace(row, col);
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

    private static void drawBlankSpace(int row, int col) {
        String backgroundColor = getTileColor(row, col);
        String textColor = Objects.equals(backgroundColor, SET_LIGHT_TILE_COLOR)
                ? SET_LIGHT_TEXT_COLOR : SET_DARK_TEXT_COLOR;
        out.print(textColor);
        out.print(EscapeSequences.WHITE_PAWN);
    }
}
