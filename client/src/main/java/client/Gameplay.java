package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import exception.ClientException;
import state.ClientState;
import state.GameplayState;
import ui.EscapeSequences;

import java.util.Scanner;
import java.util.logging.Logger;

public class Gameplay implements ClientStateInterface {
    private final ChessClient client;
    private final WebSocketFacade ws;

    Logger log = Logger.getLogger("clientLogger");

    public Gameplay(ChessClient client, WebSocketFacade ws) {
        this.client = client;
        this.ws = ws;
    }

    public String help() {
        if (client.getGameplayState() == GameplayState.OBSERVE) {
            return """
               - help (h) | display this help menu
               - leave (l) | leave the current game
               - redraw (r) | display the current chess board
               - highlight (h): <tile> | highlight legal moves for piece at given position
               """;
        }
        return """
               - help (h) | display this help menu
               - leave (l) | leave the current game
               - redraw (r) | display the current chess board
               - move (m): [A1-H8] [A1-H8] | move piece from start position to end position
               - resign (re) | forfeit the game
               - highlight (hi): <tile> | highlight legal moves for piece at given position
               """;
    }

    public String eval(String cmd, String... params) throws ClientException {
        return switch (cmd) {
            case "h", "help" -> help();
            case "l", "leave" -> leave();
            case "r", "redraw" -> client.draw();
            case "m", "move" -> makeMove(params);
            case "re", "resign" -> resign();
            case "hi", "highlight" -> highlight(params);
            default -> "Command not recognized.\n" + help();
        };
    }

    private String leave() throws ClientException {
        client.state = ClientState.POST_LOGIN;
        client.setGameplayState(null);
        ws.leave(client.getAuthorization(), client.getCurrentGameID());
        log.info(String.format("\nLeaving game [%s]\n", client.getCurrentGameID()));
        return String.format("\nLeaving game [%s]\n", client.getCurrentGameID()) + client.help();
    }

    private String makeMove(String... params) throws ClientException {
        String moveChars = validateMoveInput(params);
        ChessPosition startPosition = new ChessPosition(moveChars.charAt(1) - '0', colToNumber(moveChars.charAt(0)));
        log.fine(String.format("Starting: row %s, col %s", startPosition.getRow(), startPosition.getColumn()));
        ChessPosition endPosition = new ChessPosition(moveChars.charAt(3) - '0', colToNumber(moveChars.charAt(2)));
        log.fine(String.format("Ending: row %s, col %s", endPosition.getRow(), endPosition.getColumn()));
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        if (moveChars.length() == 5) {
            move = convertPawnPromotionMove(move, moveChars.charAt(4));
        }
        String output = String.format("Move: %s", move.toSimpleString());
        try {
            ws.makeMove(client.getAuthorization(), client.getCurrentGameID(), move);
        } catch (Exception ex) {
            log.info("makeMove exception: " + ex.getMessage());
            throw new ClientException(400, ex.getMessage());
        }
        log.info("User made move " + move);
        if (client.getGameplayState() == GameplayState.BOTH) {
            client.switchDrawState();
        }
        return output;
    }

    private String validateMoveInput(String... params) throws ClientException{
        String error = "Expected <[A1-H8]> <[A1-H8]> or <[A1-H8][A1-H8]> (Start tile, end tile)";
        String singleStringError = "Expected <[A1-H8][A1-H8]> (Start tile, end tile)";
        String doubleStringError = "Expected <[A1-H8]> <[A1-H8]> (Start tile, end tile)";
        if (params.length == 1) { // 4 char move input or 5 char pawn move input
            if (params[0].length() == 5) {
                return parseFourCharInput(params[0].substring(0,4), singleStringError) + parseCharPawnInput(params[0].charAt(4));
            }
            log.fine(String.format("Validated input %s", params[0]));
            return parseFourCharInput(params[0], singleStringError);
        } else if (params.length == 2) { // 2 x 2 char move input or 4 x 1 pawn move input
            if (params[0].length() == 4) {
                return parseFourCharInput(params[0], singleStringError) + parseCharPawnInput(params[1].charAt(0));
            }
            String startTile = params[0];
            String endTile = params[1];
            log.fine(String.format("Validated input %s", startTile + endTile));
            return parseTwoByTwoCharInput(startTile, endTile, doubleStringError);

        } else if (params.length == 3) {
            String startTile = params[0];
            String endTile = params[1];
            log.fine(String.format("Validated input %s", startTile + endTile + params[2].charAt(0)));
            return parseTwoByTwoCharInput(startTile, endTile, doubleStringError) + parseCharPawnInput(params[2].charAt(0));

        } else {
            log.fine("0 or >3 params incorrect move");
            throw new ClientException(400, error);
        }
    }

    private String parseFourCharInput(String move, String error) throws ClientException {
        move = move.toUpperCase();
        log.fine(String.format("4 char move input %s", move));
        log.fine(String.format("Char 1: %s", move.charAt(0)));
        log.fine(String.format("Char 2: %s", move.charAt(1)));
        log.fine(String.format("Char 3: %s", move.charAt(2)));
        log.fine(String.format("Char 4: %s", move.charAt(3)));
        // Validates alpha/number/alpha/number
        if (    !isValidColumn(move.charAt(0)) ||
                !isValidRow(move.charAt(1)) ||
                !isValidColumn(move.charAt(2)) ||
                !isValidRow(move.charAt(3))
        ) {
            log.fine("Incorrect move A/#/A/# pattern");
            throw new ClientException(400, error);
        }
        return move;
    }

    private String parseTwoByTwoCharInput(String startTile, String endTile, String error) throws ClientException {
        startTile = startTile.toUpperCase();
        endTile = endTile.toUpperCase();
        log.fine(String.format("2 x 2 char move input %s %s", startTile, endTile));
        // validates each tile has 2 chars
        if (startTile.length() != 2 || endTile.length() != 2) {
            log.fine("Move tiles did not have 2 chars");
            throw new ClientException(400, error);
        }
        // validates Alpha/Number Alpha/Number
        if (    !isValidColumn(startTile.charAt(0)) ||
                !isValidRow(startTile.charAt(1)) ||
                !isValidColumn(endTile.charAt(0)) ||
                !isValidRow(endTile.charAt(1))
        ) {
            log.fine("Incorrect move A/#  A/# pattern");
            throw new ClientException(400, error);
        }
        return startTile + endTile;
    }

    private String parseCharPawnInput(char c) throws ClientException {
        String output = switch (c) {
            case 'r', 'R' -> "r";
            case 'k', 'K', 'n', 'N' -> "n";
            case 'b', 'B' -> "b";
            case 'q', 'Q' -> "q";
            default -> null;
        };
        if (output == null) {
            throw new ClientException(400,  "For pawn promotion piece, add r, k, b, q to your move.");
        }
        return output;
    }

    private ChessMove convertPawnPromotionMove(ChessMove move, char c) {
        ChessPiece.PieceType type = switch (c) {
            case 'r' -> ChessPiece.PieceType.ROOK;
            case 'k' -> ChessPiece.PieceType.KNIGHT;
            case 'b' -> ChessPiece.PieceType.BISHOP;
            case 'q' -> ChessPiece.PieceType.QUEEN;
            default -> null;
        };
        return new ChessMove(move.getStartPosition(), move.getEndPosition(), type);
    }

    private Boolean isValidColumn(char c) {
        char[] validColumns = {'A', 'B','C', 'D', 'E', 'F', 'G', 'H'};
        for (char col : validColumns) {
            if (c == col) {
                return true;
            }
        }
        log.fine(String.format("Incorrect char %s", c));
        return false;
    }

    private Boolean isValidRow(char c) {
        int i = c - '0';
        int[] validRows = {1, 2, 3, 4, 5, 6, 7, 8};
        for (int row : validRows) {
            if (i == row) {
                return true;
            }
        }
        log.fine(String.format("Incorrect number %s", i));
        return false;
    }

    private int colToNumber(char c) throws ClientException {
        char[] colVals = {'A', 'B','C', 'D', 'E', 'F', 'G', 'H'};
        int[] rowVals = {1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 0; i < colVals.length; i++) {
            if (c == colVals[i]) {
                return rowVals[i];
            }
        }
        log.fine("Col to number failed");
        throw new ClientException(400, "Expected <[A1-H8]> <[A1-H8]> or <[A1-H8][A1-H8]> (Start tile, end tile)");
    }

    private String resign() throws ClientException {
        Scanner scanner = new Scanner(System.in);
        String resignPrompt =   EscapeSequences.SET_TEXT_BOLD +
                                EscapeSequences.SET_TEXT_COLOR_RED +
                                "Would you like to resign? Doing so will forfeit the game. Confirm with <y> or <yes>.";
        System.out.println(resignPrompt);
        log.info("Resign prompt");
        System.out.print(">>>\t" + EscapeSequences.RESET_TEXT_BOLD_FAINT + EscapeSequences.RESET_TEXT_COLOR);
        String response = scanner.nextLine().toLowerCase();
        if (response.equals("y") || response.equals("yes")) {
            ws.resign(client.getAuthorization(), client.getCurrentGameID());
            client.updateGameDataMap();
            log.info("User resigned");
            return "Very well. You have chosen to accept defeat.";
        }
        log.info("User did not resign");
        return "The game continues.";
    }

    private String highlight(String... params) throws ClientException {
        log.info("Highlight request");
        if (params.length == 1 && params[0].length() == 2) {
            String tile = params[0].toUpperCase();
            if (isValidColumn(tile.charAt(0)) && isValidRow(tile.charAt(1))) {
                int row = tile.charAt(1) - '0';
                int col = colToNumber(tile.charAt(0));
                ChessPosition position = new ChessPosition(row, col);
                if (client.getGameData(client.getCurrentGameID()).game().getBoard().getPiece(position) == null) {
                    return "There is not a piece at that position.";
                }
                client.drawHighlighted(position);
                return String.format("Available moves for %s", position.toSimpleString());
            }
        }
        log.fine("Invalid highlight input");
        throw new ClientException(400, "Expected piece position [A1-H8]");
    }
}
