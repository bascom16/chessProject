package client;

import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import exception.ClientException;
import state.ClientState;
import state.GameplayState;

public class Gameplay implements ClientStateInterface {
    private final ChessClient client;

    public Gameplay(ChessClient client) {
        this.client = client;
    }

    public String help() {
        if (gameplayState == GameplayState.OBSERVE) {
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

    private static GameplayState gameplayState = null;


    public static void setState(GameplayState state) {
        gameplayState = state;
    }

    public static GameplayState getGameplayState() {
        return gameplayState;
    }

    private String leave() {
        client.state = ClientState.POST_LOGIN;
        gameplayState = null;
        return String.format("\nLeaving game [%s]\n", client.getCurrentGameID()) + client.help();
    }

    private String makeMove(String... params) throws ClientException {
        String moveChars = validateMoveInput(params);
        ChessPosition startPosition = new ChessPosition(moveChars.charAt(0), colToNumber(moveChars.charAt(2)));
        ChessPosition endPosition = new ChessPosition(moveChars.charAt(2), colToNumber(moveChars.charAt(3)));
        //TODO: PROMOTION PIECE
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        client.getWs().makeMove(client.getAuthorization(), client.getCurrentGameID(), move);
        //TODO: CHECK VALID MOVE
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

    private String resign() {
//        TODO: IMPLEMENT RESIGN
        throw new RuntimeException("not implemented");
    }

    private String highlight(String... params) throws ClientException {
        if (params.length == 1) {
//            TODO: IMPLEMENT HIGHLIGHT
            throw new RuntimeException("not implemented");
        }
        throw new ClientException(400, "Expected piece position [A1-H8]");
    }
}
