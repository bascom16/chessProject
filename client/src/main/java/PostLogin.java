import exception.ResponseException;
import handler.request.CreateRequest;
import handler.request.JoinRequest;
import model.GameData;
import ui.EscapeSequences;

import java.util.Objects;

public class PostLogin implements ClientState {

    public String help() {
        return """
               
               - help (h) | displays this help menu
               - quit (q) | logout and exit the Chess program
               - logout (l) | logout current user
               - create (c): <name> | create a new Chess game under the given name
               - list (li) | lists all existing games
               - join (j): <ID> [WHITE|BLACK] | join game as specified color
               - observe (o): <ID> | observe game
               """;
    }

    public String eval(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
            case "q", "quit" -> quit();
            case "l", "logout" -> logout();
            case "c", "create" -> create(params);
            case "li", "list" -> list();
            case "j", "join" -> join(params);
            case "o", "observe" -> observe(params);
            default -> help();
        };
    }

    private String quit() throws ResponseException {
        logout();
        return "quit";
    }

    private String logout() throws ResponseException {
        ChessClient.clearGameDataMap();
        ChessClient.server.logout(ChessClient.authData.authToken());
        ChessClient.state = State.PRE_LOGIN;
        return "Successfully logged out";
    }

    private String create(String... params) throws ResponseException {
        if (params.length == 1) {
            String gameName = params[0];
            int gameID = ChessClient.server.create(new CreateRequest(gameName), ChessClient.authData.authToken());
            updateGameDataMap();
            return String.format("Created game [%s] as game number [%s]", gameName, gameID);
        }
        throw new ResponseException(400, "Expected <name>");
    }

    private String list() throws ResponseException {
        updateGameDataMap();
        return EscapeSequences.SET_TEXT_BOLD +
                "\nList of games: \n" +
                EscapeSequences.RESET_TEXT_BOLD_FAINT +
                ChessClient.readGameDataMap();
    }

    private GameData[] getGameData() throws ResponseException {
        return ChessClient.server.list(ChessClient.getAuthorization());
    }

    private void updateGameDataMap() throws ResponseException {
        ChessClient.fillGameDataMap(getGameData());
    }

    private String join(String... params) throws ResponseException {
        String error = "Expected <ID> [WHITE|BLACK]";
        if (params.length == 2) {
            if (!canConvertToInt(params[0])) {
                String detail = EscapeSequences.SET_TEXT_ITALIC +
                                "ID must be a valid number" +
                                EscapeSequences.RESET_TEXT_ITALIC;
                throw new ResponseException(400, error + detail);
            }
            int gameID = Integer.parseInt(params[0]);
            String color = params[1].toLowerCase();
            if (!(Objects.equals(color, "white") || Objects.equals(color, "black"))) {
                String detail = EscapeSequences.SET_TEXT_ITALIC +
                                "Color must be \"white\" or \"black\"" +
                                EscapeSequences.RESET_TEXT_ITALIC;
                throw new ResponseException(400, error + detail);
            }
            ChessClient.server.join(new JoinRequest(color.toUpperCase(), gameID), ChessClient.getAuthorization());
            ChessClient.state = State.GAMEPLAY;
            ChessClient.setCurrentGameID(gameID);
            return String.format("Joined game [%s] as [%s]", gameID, color.toUpperCase());
        }
        throw new ResponseException(400, error);
    }

    private static boolean canConvertToInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String observe(String... params) {
        throw new RuntimeException("Not implemented");
    }
}
