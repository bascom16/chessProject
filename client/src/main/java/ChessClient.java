import exception.ResponseException;
import server.Server;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    protected static ServerFacade server;
    private final String serverURL;
    protected static State state = State.PRE_LOGIN;

    private static final PreLogin preLogin = new PreLogin();
    private static final PostLogin postLogin = new PostLogin();
    private static final Gameplay gameplay = new Gameplay();

    ChessClient(String serverURL) {
        server = new ServerFacade(serverURL);
        this.serverURL = serverURL;
    }

    public String help(State state) {
        return getStateObject(state).help();
    }

    private ClientState getStateObject(State state) {
        return switch (state) {
            case PRE_LOGIN -> preLogin;
            case POST_LOGIN -> postLogin;
            case GAMEPLAY -> gameplay;
        };
    }

    public String eval (String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return getStateObject(state).eval(cmd);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
