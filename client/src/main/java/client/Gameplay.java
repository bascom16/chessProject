package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import exception.ClientException;
import model.GameData;
import state.ClientState;
import state.GameplayState;
import ui.DrawChessBoard;
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
        client.fillGameDataMap();
        ws.leave(client.getAuthorization(), client.getCurrentGameID());
        log.info(String.format("\nLeaving game [%s]\n", client.getCurrentGameID()));
        return String.format("\nLeaving game [%s]\n", client.getCurrentGameID()) + client.help();
    }

    private String makeMove(String... params) throws ClientException {
        validateMyTurn();
        String moveChars = validateMoveInput(params);
        ChessPosition startPosition = new ChessPosition(moveChars.charAt(0), colToNumber(moveChars.charAt(2)));
        ChessPosition endPosition = new ChessPosition(moveChars.charAt(2), colToNumber(moveChars.charAt(3)));
        //TODO: PROMOTION PIECE
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        ws.makeMove(client.getAuthorization(), client.getCurrentGameID(), move);
        log.info("User made move " + move);
        return String.format("Moving %s to %s", startPosition.toSimpleString(), endPosition.toSimpleString());
    }

    private String validateMoveInput(String... params) throws ClientException{
        String error = "Expected <[A1-H8]> <[A1-H8]> or <[A1-H8][A1-H8]> (Start tile, end tile)";
        if (params.length == 1) { // 4 char move input
            String move = params[0];
            // Validates alpha/number/alpha/number
            if (    !isValidColumn(move.charAt(0)) ||
                    !isValidRow(move.charAt(1)) ||
                    !isValidColumn(move.charAt(2)) ||
                    !isValidRow(move.charAt(3))
            ) {
                throw new ClientException(400, error);
            }
            return move;
        } else if (params.length == 2) { // 2 x 2 char move input
            String startTile = params[0].toUpperCase();
            String endTile = params[1].toUpperCase();
            // validates each tile has 2 chars
            if (startTile.length() != 2 || endTile.length() != 2) {
                throw new ClientException(400, error);
            }
            // validates Alpha/Number Alpha/Number
            if (    !isValidColumn(startTile.charAt(0)) ||
                    !isValidRow(startTile.charAt(1)) ||
                    !isValidColumn(endTile.charAt(0)) ||
                    !isValidRow(endTile.charAt(1))
            ) {
                throw new ClientException(400, error);
            }
            return startTile + endTile;
        } else {
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
        return false;
    }

    private Boolean isValidRow(int i) {
        int[] validRows = {1, 2, 3, 4, 5, 6, 7, 8};
        for (int row : validRows) {
            if (i == row) {
                return true;
            }
        }
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
        throw new ClientException(400, "Expected <[A1-H8]> <[A1-H8]> or <[A1-H8][A1-H8]> (Start tile, end tile)");
    }

    private String resign() throws ClientException {
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
                int row = tile.charAt(1);
                int col = colToNumber(tile.charAt(0));
                ChessPosition position = new ChessPosition(row, col);
                client.drawHighlighted(position);
                return String.format("Available moves for %s", position.toSimpleString());
            }
        }
        throw new ClientException(400, "Expected piece position [A1-H8]");
    }

    private void validateMyTurn() throws ClientException {
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
