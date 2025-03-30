package client;

import exception.ClientException;
import model.GameData;
import server.ServerFacade;
import model.AuthData;
import state.ClientState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ChessClient {
    protected static ServerFacade server;
    protected static ClientState state = ClientState.PRE_LOGIN;
    private static AuthData authData;

    private static final PreLogin PRE_LOGIN = new PreLogin();
    private static final PostLogin POST_LOGIN = new PostLogin();
    private static final Gameplay GAMEPLAY = new Gameplay();

    ChessClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public static String help() {
        return getStateObject(state).help();
    }

    private static ClientStateInterface getStateObject(ClientState state) {
        return switch (state) {
            case ClientState.PRE_LOGIN -> PRE_LOGIN;
            case ClientState.POST_LOGIN -> POST_LOGIN;
            case ClientState.GAMEPLAY -> GAMEPLAY;
        };
    }

    public String eval (String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return getStateObject(state).eval(cmd.toLowerCase(), params);
        } catch (ClientException ex) {
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

    private static final HashMap<Integer, GameData> GAME_DATA_MAP = new HashMap<>();

    public static int getNumGames() {
        return GAME_DATA_MAP.size();
    }

    public static GameData getGameData(int gameID) {
        return GAME_DATA_MAP.get(gameID);
    }

    public static void fillGameDataMap(GameData... params) {
        clearGameDataMap();
        for (GameData game : params) {
            GAME_DATA_MAP.put(game.gameID(), game);
        }
    }

    public static void clearGameDataMap() {
        GAME_DATA_MAP.clear();
    }

    public static String readGameDataMap() {
        StringBuilder listGames = new StringBuilder();
        for (int i = 1; i < GAME_DATA_MAP.size() + 1; i++) {
            GameData game = GAME_DATA_MAP.get(i);
            listGames.append(readGame(game));
            listGames.append("\n");
        }
        return listGames.toString();
    }

    private static String readGame(GameData game) {
        int padWhite = 35;
        int padBlack = padWhite + 25;

        String leftSide = " - #" +
                game.gameID() +
                " [" +
                game.gameName() +
                "] ";
        String left = padString(leftSide, padWhite);

        StringBuilder middleSide = new StringBuilder();
        middleSide.append(left);
        middleSide.append(" | ");
        String white = game.whiteUsername() == null ? "<Available>" : "[" + game.whiteUsername() + "]";
        middleSide.append(white);
        String middle = padString(middleSide.toString(), padBlack);

        StringBuilder rightSide = new StringBuilder();
        rightSide.append(middle);
        rightSide.append(" | ");
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
        else {
            return false;
        }
    }
}
