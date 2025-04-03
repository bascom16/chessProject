package client;

import exception.ClientException;
import handler.request.LoginRequest;
import handler.request.RegisterRequest;
import state.ClientState;

import java.util.logging.Logger;

public class PreLogin implements ClientStateInterface {
    private final ChessClient client;

    Logger log = Logger.getLogger("clientLogger");

    public PreLogin(ChessClient client) {
        this.client = client;
    }

    public String help() {
        return """
               - help (h) | display this help menu
               - quit (q) | exit the Chess program
               - login (li): <username> <password> | login existing user
               - register (r): <username> <password> <email> | register new user
               - [DEBUG] clear | clear database
               """;
    }

    public String eval(String cmd, String... params) throws ClientException {
        return switch (cmd) {
            case "h", "help" -> help();
            case "q", "quit" -> quit();
            case "li", "login" -> login(params);
            case "r", "register" -> register(params);
            case "clear" -> clear();
            default -> "Command not recognized.\n" + help();
        };
    }

    private String quit() {
        log.info("User quit client");
        return "quit";
    }

    private String login(String... params) throws ClientException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            client.setAuthData(client.server.login(new LoginRequest(username, password)));
            client.state = ClientState.POST_LOGIN;
            log.info("Logged in user " + username);
            return String.format("You signed in as user [%s]\n", username) + client.help();
        }
        throw new ClientException(400, "Expected <username> <password>");
    }

    private String register(String... params) throws ClientException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            client.setAuthData(client.server.register(new RegisterRequest(username, password, email)));
            client.state = ClientState.POST_LOGIN;
            log.info("Registered user " + username);
            return String.format("You registered as new user [%s]\n", username) + client.help();
        }
        throw new ClientException(400, "Expected <username> <password> <email>");
    }

    private String clear() throws ClientException {
        client.server.clear();
        client.clearGameDataMap();
        log.info("Cleared database");
        return "Cleared database";
    }
}
