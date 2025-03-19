import exception.ResponseException;
import state.GameplayState;

public class Gameplay implements ClientState {

    public String help() {
        return """
               
               - help (h) | displays this help menu
               - quit (q) | logout and exit the Chess program
               - logout (l) | logout current user
               - exit (e) | exit current game
               - TBD | Have patience, young padawan
               """;
    }

    public String eval(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
            case "q", "quit" -> quit();
            case "l", "logout" -> logout();
            case "e", "exit" -> exit();
            default -> help();
        };
    }

    private String quit() throws ResponseException {
        logout();
        return "quit";
    }

    private String logout() throws ResponseException {
        ChessClient.clearGameDataMap();
        ChessClient.server.logout(ChessClient.getAuthorization());
        ChessClient.state = State.PRE_LOGIN;
        return "Successfully logged out" + ChessClient.help();
    }

    private String exit() {
        ChessClient.state = State.POST_LOGIN;
        return "Exited game" + ChessClient.help();
    }

    private static GameplayState gameplayState = null;

    public static void setState(GameplayState state) {
        gameplayState = state;
    }

    public static GameplayState getGameplayState() {
        return gameplayState;
    }
}
