package client;

import exception.ClientException;
import state.ClientState;
import state.GameplayState;

public class Gameplay implements ClientStateInterface {

    public String help() {
        return """
               - help (h) | display this help menu
               - quit (q) | logout and exit the Chess program
               - logout (lo) | logout current user
               - exit (e) | exit current game
               - TBD | Have patience, young padawan
               """;
    }

    public String eval(String cmd, String... params) throws ClientException {
        return switch (cmd) {
            case "h", "help" -> help();
            case "q", "quit" -> quit();
            case "lo", "logout" -> logout();
            case "e", "exit" -> exit();
            default -> "Command not recognized.\n" + help();
        };
    }

    private String quit() throws ClientException {
        logout();
        return "quit";
    }

    private String logout() throws ClientException {
        drawState = GameplayState.WHITE;
        ChessClient.clearGameDataMap();
        ChessClient.server.logout(ChessClient.getAuthorization());
        ChessClient.state = ClientState.PRE_LOGIN;
        return "Successfully logged out" + ChessClient.help();
    }

    private String exit() {
        drawState = GameplayState.WHITE;
        ChessClient.state = ClientState.POST_LOGIN;
        ChessClient.setCurrentGameID(0);
        return "Exited game\n" + ChessClient.help();
    }

    private static GameplayState gameplayState = null;
    private static GameplayState drawState = GameplayState.WHITE;

    public static GameplayState getDrawState() {
        return drawState;
    }

    public static void switchDrawState() {
        drawState = (drawState == GameplayState.WHITE) ? GameplayState.BLACK : GameplayState.WHITE;
    }

    public static void setState(GameplayState state) {
        gameplayState = state;
    }

    public static GameplayState getGameplayState() {
        return gameplayState;
    }
}
