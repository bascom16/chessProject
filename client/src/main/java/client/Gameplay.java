package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import exception.ClientException;
import model.GameData;
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
        validateMyTurn();
        String moveChars = validateMoveInput(params);
        log.fine(moveChars);
        log.fine(String.valueOf(moveChars.charAt(0)));
        log.fine(String.valueOf(moveChars.charAt(1)));
        log.fine(String.valueOf(moveChars.charAt(2)));
        log.fine(String.valueOf(moveChars.charAt(3)));
        ChessPosition startPosition = new ChessPosition(moveChars.charAt(1) - '0', colToNumber(moveChars.charAt(0)));
        log.fine(String.format("Starting: row %s, col %s", startPosition.getRow(), startPosition.getColumn()));
        ChessPosition endPosition = new ChessPosition(moveChars.charAt(3) - '0', colToNumber(moveChars.charAt(2)));
        log.fine(String.format("Ending: row %s, col %s", endPosition.getRow(), endPosition.getColumn()));
        //TODO: PROMOTION PIECE
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        ws.makeMove(client.getAuthorization(), client.getCurrentGameID(), move);
        log.info("User made move " + move);
        String pieceType = client.getGameData
                (client.getCurrentGameID()).game().getBoard().getPiece(startPosition).getPieceType().toString();
        return String.format("Moving %s %s to %s",  pieceType.toLowerCase(),
                                                    startPosition.toSimpleString(),
                                                    endPosition.toSimpleString());
    }

    private String validateMoveInput(String... params) throws ClientException{
        String error = "Expected <[A1-H8]> <[A1-H8]> or <[A1-H8][A1-H8]> (Start tile, end tile)";
        if (params.length == 1) { // 4 char move input
            String move = params[0].toUpperCase();
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
            log.fine(String.format("Validated input %s", move));
            return move;
        } else if (params.length == 2) { // 2 x 2 char move input
            String startTile = params[0].toUpperCase();
            String endTile = params[1].toUpperCase();
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
            log.fine(String.format("Validated input %s", startTile + endTile));
            return startTile + endTile;
        } else {
            log.fine("0 or >3 params incorrect move");
            throw new ClientException(400, error);
        }
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
        log.fine(String.format("Int input i=%s", i));
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
        client.validateGameNotOver(client.getCurrentGameID());
        Scanner scanner = new Scanner(System.in);
        String resignPrompt = EscapeSequences.SET_TEXT_BOLD +
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
                client.drawHighlighted(position);
                return String.format("Available moves for %s", position.toSimpleString());
            }
        }
        log.fine("Invalid highlight input");
        throw new ClientException(400, "Expected piece position [A1-H8]");
    }

    private void validateMyTurn() throws ClientException {
        client.validateGameNotOver(client.getCurrentGameID());
        GameData gameData = client.getGameData(client.getCurrentGameID());
        ChessGame.TeamColor teamTurn = gameData.game().getTeamTurn();
        if ( teamTurn == ChessGame.TeamColor.WHITE && client.getGameplayState() == GameplayState.WHITE) {
            return;
        } else if ( teamTurn == ChessGame.TeamColor.BLACK && client.getGameplayState() == GameplayState.BLACK) {
            return;
        }
        log.info("User acted out of turn");
        throw new ClientException(400, "It's not your turn! Please be patient.");
    }


}
