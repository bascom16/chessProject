package client;

import chess.ChessBoard;
import exception.ClientException;
import handler.request.CreateRequest;
import handler.request.JoinRequest;
import model.GameData;
import state.ClientState;
import state.GameplayState;
import ui.DrawChessBoard;
import ui.EscapeSequences;

import java.util.Objects;

public class PostLogin implements ClientStateInterface {

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
        ChessClient.clearGameDataMap();
        ChessClient.setCurrentGameID(0);
        ChessClient.server.logout(ChessClient.getAuthorization());
        ChessClient.state = ClientState.PRE_LOGIN;
        return "Successfully logged out\n" + ChessClient.help();
    }

    private String create(String... params) throws ClientException {
        if (params.length == 1) {
            String gameName = params[0];
            int gameID = ChessClient.server.create(new CreateRequest(gameName), ChessClient.getAuthorization());
            updateGameDataMap();
            return String.format("Created game [%s] as game number [%s]", gameName, gameID);
        }
        throw new ClientException(400, "Expected <name>");
    }

    private String list() throws ClientException {
        updateGameDataMap();
        String message =    EscapeSequences.SET_TEXT_BOLD +
                            "List of games: \n" +
                            EscapeSequences.RESET_TEXT_BOLD_FAINT;
        String data = ChessClient.readGameDataMap();
        return message + ((data.isBlank()) ? "No current games" : data);
    }

    private GameData[] getGameData() throws ClientException {
        return ChessClient.server.list(ChessClient.getAuthorization());
    }

    private void updateGameDataMap() throws ClientException {
        ChessClient.fillGameDataMap(getGameData());
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
            if (ChessClient.userIsInGameAsColor(gameID, color)) {
                joinUpdate(gameID, color);
                return String.format("Reentering game [%s] as %s\n", gameID, color) + ChessClient.help();
            }
            // User already in game as opposite color
            String otherColor = color.equals("white") ? "black" : "white";
            if (ChessClient.userIsInGameAsColor(gameID, otherColor)) {
                throw new ClientException(400, String.format("User already in game [%s] as %s", gameID, otherColor));
            }
            // Successful new join
            ChessClient.server.join(new JoinRequest(color.toUpperCase(), gameID), ChessClient.getAuthorization());
            joinUpdate(gameID, color);
            return String.format("\nJoined game [%s] as [%s]\n", gameID, color.toUpperCase()) + ChessClient.help();
        }
        throw new ClientException(400, error);
    }

    private void joinUpdate(int gameID, String color) throws ClientException {
        updateGameDataMap();
        ChessClient.state = ClientState.GAMEPLAY;
        ChessClient.setCurrentGameID(gameID);
        GameplayState joinState;
        if (color.equals("white")) {
            joinState = GameplayState.WHITE;
        } else {
            joinState = GameplayState.BLACK;
        }
        Gameplay.setState(joinState);
        ChessBoard board = ChessClient.getGameData(gameID).game().getBoard();
        DrawChessBoard.drawBoard(board, System.out, joinState);
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
        if (gameID <= 0 || gameID > ChessClient.getNumGames()) {
            throw new ClientException(400, String.format("Game [%s] not found", gameID));
        }
    }

    private String observe(String... params) throws ClientException {
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

            ChessClient.state = ClientState.GAMEPLAY;
            ChessClient.setCurrentGameID(gameID);
            Gameplay.setState(GameplayState.OBSERVE);

            ChessBoard board = ChessClient.getGameData(gameID).game().getBoard();
            DrawChessBoard.drawBoard(board, System.out, GameplayState.OBSERVE);
            return String.format("\nObserving game [%s]\n", gameID) + ChessClient.help();
        }
        throw new ClientException(400, error);
    }
}
