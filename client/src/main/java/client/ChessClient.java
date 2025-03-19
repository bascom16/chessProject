package client;

import model.GameData;
import server.ServerFacade;
import model.AuthData;
import state.ClientState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ChessClient {
    protected static ServerFacade server;
    private final String serverURL;
    protected static ClientState state = ClientState.PRE_LOGIN;
    private static AuthData authData;

    private static final PreLogin preLogin = new PreLogin();
    private static final PostLogin postLogin = new PostLogin();
    private static final Gameplay gameplay = new Gameplay();

    ChessClient(String serverURL) {
        server = new ServerFacade(serverURL);
        this.serverURL = serverURL;
    }

    public static String help() {
        return getStateObject(state).help();
    }

    private static ClientStateInterface getStateObject(ClientState state) {
        return switch (state) {
            case ClientState.PRE_LOGIN -> preLogin;
            case ClientState.POST_LOGIN -> postLogin;
            case ClientState.GAMEPLAY -> gameplay;
        };
    }

    public String eval (String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return getStateObject(state).eval(cmd.toLowerCase(), params);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private static int currentGameID = 0;

    public static int getCurrentGameID() {
        return currentGameID;
    }

    public static void setCurrentGameID(int currentGameID) {
        ChessClient.currentGameID = currentGameID;
    }

    private static final HashMap<Integer, GameData> gameDataMap = new HashMap<>();

    public static int getNumGames() {
        return gameDataMap.size();
    }

    public static GameData getGameData(int gameID) {
        return gameDataMap.get(gameID);
    }

    public static void fillGameDataMap(GameData... params) {
        clearGameDataMap();
        for (GameData game : params) {
            gameDataMap.put(game.gameID(), game);
        }
    }

    public static void clearGameDataMap() {
        gameDataMap.clear();
    }

    public static String readGameDataMap() {
        StringBuilder listGames = new StringBuilder();
        for (int i = 1; i < gameDataMap.size() + 1; i++) {
            GameData game = gameDataMap.get(i);
            listGames.append(readGame(game));
            listGames.append("\n");
        }
        return listGames.toString();
    }

    private static String readGame(GameData game) {
        StringBuilder leftSide = new StringBuilder();
        leftSide.append(" - #");
        leftSide.append(game.gameID());
        leftSide.append(" [");
        leftSide.append(game.gameName());
        leftSide.append("] ");
        String left = leftSide.toString();
        left = padString(left, 20);

        StringBuilder middleSide = new StringBuilder();
        middleSide.append(left);
        middleSide.append("| ");
        middleSide.append("White - ");
        String white = game.whiteUsername() == null ? "<Available>" : "[" + game.whiteUsername() + "]";
        middleSide.append(white);
        String middle = middleSide.toString();
        middle = padString(middle, 20 + 30);

        StringBuilder rightSide = new StringBuilder();
        rightSide.append(middle);
        rightSide.append(" | Black - ");
        String black = game.blackUsername() == null ? "<Available>" : "[" + game.blackUsername() + "]";
        rightSide.append(black);
        return rightSide.toString();
    }

    private static String padString(String left, int maxPadLength) {
        int padLength = maxPadLength - left.length();
        padLength = Math.max(padLength, 0);
        String pad = " ".repeat(padLength);
        return left + pad;
    }

    public static void setAuthData(AuthData data) {
        authData = data;
    }

    public static String getAuthorization() {
        return authData != null ? authData.authToken() : null;
    }

    public static String getUsername() {
        return authData != null ? authData.username() : null;
    }

    public static boolean userIsInGameAsColor(int gameID, String color) {
        GameData game = getGameData(gameID);
        if (game == null) {
            return false;
        }
        String username = authData.username();
        if (Objects.equals(color, "white")) {
            return Objects.equals(username, game.whiteUsername());
        } else if (Objects.equals(color, "black")) {
            return Objects.equals(username, game.blackUsername());
        }
        else return false;
    }
}
