package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
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
import java.util.logging.Logger;

public class ChessClient {
    protected ServerFacade server;
    protected ClientState state = ClientState.PRE_LOGIN;

    private WebSocketFacade ws = null;

    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final Gameplay gameplay;

    Logger log = Logger.getLogger("clientLogger");

    public ChessClient(String serverURL, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverURL);

        try {
            ws = new WebSocketFacade(serverURL, notificationHandler, this);
        } catch (ClientException ex) {
            System.out.println("Error! Unable to initialize client");
            log.warning("Unable to initialize client websocket in construction.");
        }

        preLogin = new PreLogin(this);
        postLogin = new PostLogin(this);
        gameplay = new Gameplay(this, ws);
    }

    public String help() {
        return getStateObject(state).help();
    }

    private ClientStateInterface getStateObject(ClientState state) {
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
        log.info("Updated Client gameData hashMap");
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

    private GameData[] getGameDataFromServer() throws ClientException {
        return server.list(getAuthorization());
    }

    public void updateGameDataMap() throws ClientException {
        fillGameDataMap(getGameDataFromServer());
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

    public void switchDrawState() {
        drawState = (drawState == GameplayState.WHITE) ? GameplayState.BLACK : GameplayState.WHITE;
    }

    public String draw() {
        log.info("Draw board request");
        ChessBoard board = getGameData(getCurrentGameID()).game().getBoard();
        DrawChessBoard.drawBoard(board, System.out, drawState);
        return "";
    }

    public void drawHighlighted(ChessPosition position) {
        log.info(String.format("Draw highlighted board request for %s", position));
        ChessGame game = getGameData(getCurrentGameID()).game();
        DrawChessBoard.drawHighlightedBoard(game, System.out, drawState, position);
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

    public void loadGame(GameData gameData) {
        log.info("Added game to Client gameData hashMap");
        GAME_DATA_MAP.put(currentGameID, gameData);
        draw();
    }

    public void connect() throws ClientException {
        ws.connect(getAuthorization(), getCurrentGameID());
    }
}
