package client;

import exception.ClientException;
import handler.request.LoginRequest;
import handler.request.RegisterRequest;
import state.ClientState;

public class PreLogin implements ClientStateInterface {

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
        return "quit";
    }

    private String login(String... params) throws ClientException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            ChessClient.setAuthData(ChessClient.server.login(new LoginRequest(username, password)));
            ChessClient.state = ClientState.POST_LOGIN;
            return String.format("You signed in as user [%s]\n", username) + ChessClient.help();
        }
        throw new ClientException(400, "Expected <username> <password>");
    }

    private String register(String... params) throws ClientException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            ChessClient.setAuthData(ChessClient.server.register(new RegisterRequest(username, password, email)));
            ChessClient.state = ClientState.POST_LOGIN;
            return String.format("You registered as new user [%s]\n", username) + ChessClient.help();
        }
        throw new ClientException(400, "Expected <username> <password> <email>");
    }

    private String clear() throws ClientException {
        ChessClient.server.clear();
        ChessClient.clearGameDataMap();
        return "Cleared database";
    }
}
