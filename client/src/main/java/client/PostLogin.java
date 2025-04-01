package client;

import exception.ClientException;
import handler.request.CreateRequest;
import handler.request.JoinRequest;
import model.GameData;
import state.ClientState;
import state.GameplayState;
import ui.EscapeSequences;

import java.util.Objects;

public class PostLogin implements ClientStateInterface {
    private final ChessClient client;

    public PostLogin(ChessClient client) {
        this.client = client;
    }

    public String help() {
        return """
               - help (h) | display this help menu
               - quit (q) | logout and exit the Chess program
               - logout (lo) | logout current user
               - create (c): <name> | create a new Chess game
               - list (l) | lists all existing games
               - play (p): <ID> [WHITE|BLACK] | join game as color
               - observe (o): <ID> | observe game
               """;
    }

    public String eval(String cmd, String... params) throws ClientException {
        return switch (cmd) {
            case "h", "help" -> help();
            case "q", "quit" -> quit();
            case "lo", "logout" -> logout();
            case "c", "create" -> create(params);
            case "l", "list" -> list();
            case "p", "play" -> join(params);
            case "o", "observe" -> observe(params);
            default -> "Command not recognized.\n" + help();
        };
    }

    private String quit() throws ClientException {
        logout();
        return "quit";
    }

    private String logout() throws ClientException {
        client.clearGameDataMap();
        client.setCurrentGameID(0);
        client.server.logout(client.getAuthorization());
        client.state = ClientState.PRE_LOGIN;
        return "Successfully logged out\n" + client.help();
    }

    private String create(String... params) throws ClientException {
        if (params.length >= 1) {
            StringBuilder builder = new StringBuilder();
            for (String word: params) {
                builder.append(word);
                builder.append(" ");
            }
            builder.append("\b");
            String gameName = builder.toString();
            int gameID = client.server.create(new CreateRequest(gameName), client.getAuthorization());
            updateGameDataMap();
            return String.format("Created game [%s] as game number [%s]", gameName, gameID);
        }
        throw new ClientException(400, "Expected <name>");
    }

    private String list() throws ClientException {
        updateGameDataMap();
        int widthWhite = 25;
        int widthBlack = 20;
        StringBuilder builder = new StringBuilder();
        builder.append(EscapeSequences.SET_TEXT_BOLD);
        builder.append("List of games:");
        builder.append(" ".repeat(widthWhite));
        builder.append(EscapeSequences.SET_TEXT_UNDERLINE);
        builder.append("White");
        builder.append(EscapeSequences.RESET_TEXT_UNDERLINE);
        builder.append(" ".repeat(widthBlack));
        builder.append(EscapeSequences.SET_TEXT_UNDERLINE);
        builder.append("Black\n");
        builder.append(EscapeSequences.RESET_TEXT_UNDERLINE);
        builder.append(EscapeSequences.RESET_TEXT_BOLD_FAINT);
        String data = client.readGameDataMap();
        return builder + ((data.isBlank()) ? "No current games" : data);
    }

    private GameData[] getGameData() throws ClientException {
        return client.server.list(client.getAuthorization());
    }

    private void updateGameDataMap() throws ClientException {
        client.fillGameDataMap(getGameData());
    }

    private String join(String... params) throws ClientException {
        updateGameDataMap();
        String error = "Expected <ID> [WHITE|BLACK]";
        if (params.length == 2) {
            // invalid gameID
            if (incompatibleConvertToInt(params[0])) {
                String detail = EscapeSequences.SET_TEXT_ITALIC +
                                " ID must be a valid number" +
                                EscapeSequences.RESET_TEXT_ITALIC;
                throw new ClientException(400, error + detail);
            }
            int gameID = Integer.parseInt(params[0]);
            checkGameExists(gameID);
            // Invalid color
            String color = params[1].toLowerCase();
            if (!(Objects.equals(color, "white") || Objects.equals(color, "black"))) {
                String detail = EscapeSequences.SET_TEXT_ITALIC +
                                " Color must be \"white\" or \"black\"" +
                                EscapeSequences.RESET_TEXT_ITALIC;
                throw new ClientException(400, error + detail);
            }
            // Reentry
            if (client.userIsInGameAsColor(gameID, color)) {
                joinUpdate(gameID, color);
                return String.format("Reentering game [%s] as %s\n", gameID, color) + client.help();
            }
            // User already in game as opposite color
            String otherColor = color.equals("white") ? "black" : "white";
            if (client.userIsInGameAsColor(gameID, otherColor)) {
                client.server.join(new JoinRequest(color.toUpperCase(), gameID), client.getAuthorization());
                joinUpdate(gameID, color);
                if (color.equals("white")) {
                    client.switchDrawState();
                }
                return String.format("Joining game [%s] as [WHITE] and [BLACK]", gameID);
            }
            // Successful new join
            client.server.join(new JoinRequest(color.toUpperCase(), gameID), client.getAuthorization());
            joinUpdate(gameID, color);
            return String.format("\nJoined game [%s] as [%s]\n", gameID, color.toUpperCase()) + client.help();
        }
        throw new ClientException(400, error);
    }

    private void joinUpdate(int gameID, String color) throws ClientException {
        updateGameDataMap();
        client.state = ClientState.GAMEPLAY;
        client.setCurrentGameID(gameID);
        GameplayState joinState = switch (color) {
            case "both" -> GameplayState.BOTH;
            case "black" -> GameplayState.BLACK;
            case "white" -> GameplayState.WHITE;
            default -> throw new ClientException(400, "Invalid color");
        };
        Gameplay.setState(joinState);
        if (color.equals("black")) {
            client.switchDrawState();
        }
        client.draw();
    }

    private static boolean incompatibleConvertToInt(String s) {
        try {
            Integer.parseInt(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private void checkGameExists(int gameID) throws ClientException {
        if (gameID <= 0 || gameID > client.getNumGames()) {
            throw new ClientException(400, String.format("Game [%s] not found", gameID));
        }
    }

    private String observe(String... params) throws ClientException {
        updateGameDataMap();
        String error = "Expected <ID>";
        if (params.length == 1) {
            if (incompatibleConvertToInt(params[0])) {
                String detail = EscapeSequences.SET_TEXT_ITALIC +
                        " ID must be a valid number" +
                        EscapeSequences.RESET_TEXT_ITALIC;
                throw new ClientException(400, error + detail);
            }
            int gameID = Integer.parseInt(params[0]);
            checkGameExists(gameID);

            client.state = ClientState.GAMEPLAY;
            client.setCurrentGameID(gameID);
            Gameplay.setState(GameplayState.OBSERVE);

            client.draw();
            return String.format("\nObserving game [%s]\n", gameID) + client.help();
        }
        throw new ClientException(400, error);
    }
}
