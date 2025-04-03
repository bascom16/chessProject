package client;

import chess.ChessBoard;
import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ClientException;
import model.GameData;
import server.ServerFacade;
import model.AuthData;
import state.ClientState;
import state.GameplayState;
import ui.DrawChessBoard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ChessClient {
    private final NotificationHandler notificationHandler;
    private final String serverURL;
    protected ServerFacade server;
    protected ClientState state = ClientState.PRE_LOGIN;

    private final PreLogin PRE_LOGIN = new PreLogin(this);
    private final PostLogin POST_LOGIN = new PostLogin(this);
    private final Gameplay GAMEPLAY = new Gameplay(this);

    public ChessClient(String serverURL, NotificationHandler notificationHandler) {
        this.serverURL = serverURL;
        server = new ServerFacade(serverURL);
        this.notificationHandler = notificationHandler;
    }

    public String help() {
        return getStateObject(state).help();
    }

    private ClientStateInterface getStateObject(ClientState state) {
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

    // GameID functions
    private int currentGameID = 0;

    public int getCurrentGameID() {
        return currentGameID;
    }

    public void setCurrentGameID(int currentGameID) {
        this.currentGameID = currentGameID;
    }

    // Game data functions
    private final HashMap<Integer, GameData> GAME_DATA_MAP = new HashMap<>();

    public int getNumGames() {
        return GAME_DATA_MAP.size();
    }

    public GameData getGameData(int gameID) {
        return GAME_DATA_MAP.get(gameID);
    }

    public void fillGameDataMap(GameData... params) {
        clearGameDataMap();
        for (GameData game : params) {
            GAME_DATA_MAP.put(game.gameID(), game);
        }
    }

    public void clearGameDataMap() {
        GAME_DATA_MAP.clear();
    }

    public String readGameDataMap() {
        StringBuilder listGames = new StringBuilder();
        for (int i = 1; i < GAME_DATA_MAP.size() + 1; i++) {
            GameData game = GAME_DATA_MAP.get(i);
            listGames.append(displayGame(game));
            listGames.append("\n");
        }
        return listGames.toString();
    }

    private static String displayGame(GameData game) {
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

    // Authorization data and functions
    private AuthData authData;

    public void setAuthData(AuthData data) {
        authData = data;
    }

    public String getAuthorization() {
        return authData != null ? authData.authToken() : null;
    }

    public boolean userIsInGameAsColor(int gameID, String color) {
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

    // Draw state and functions
    private GameplayState drawState = GameplayState.WHITE;

    public GameplayState getDrawState() {
        return drawState;
    }

    public void switchDrawState() {
        drawState = (drawState == GameplayState.WHITE) ? GameplayState.BLACK : GameplayState.WHITE;
    }

    public String draw() {
        ChessBoard board = getGameData(getCurrentGameID()).game().getBoard();
        DrawChessBoard.drawBoard(board, System.out, drawState);
        return "";
    }

    // Gameplay State: White, Black, Observe, Both
    private GameplayState gameplayState = null;

    public void setGameplayState(GameplayState state) {
        gameplayState = state;
    }

    public GameplayState getGameplayState() {
        return gameplayState;
    }

    // Web socket methods
    private WebSocketFacade ws;

    public void initializeWebSocket() throws ClientException {
        if (ws == null) {
            ws = new WebSocketFacade(serverURL, notificationHandler, this);
        }
    }

    public WebSocketFacade getWs() {
        return ws;
    }

    public void loadGame(GameData gameData) {
        GAME_DATA_MAP.put(getCurrentGameID(), gameData);
        draw();
    }
}
