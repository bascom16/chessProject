package client;

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
        if (params.length == 2) {
//            TODO: IMPLEMENT MAKE MOVE
            throw new RuntimeException("not implemented");
        }
        throw new ClientException(400, "Expected <start position [A1-H8]> <end position [A1-H8]>");
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
