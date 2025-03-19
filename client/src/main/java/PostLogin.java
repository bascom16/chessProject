import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import handler.request.CreateRequest;
import handler.request.JoinRequest;
import model.GameData;
import state.GameplayState;
import ui.DrawChessBoard;
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
        ChessClient.server.logout(ChessClient.getAuthorization());
        ChessClient.state = State.PRE_LOGIN;
        return "Successfully logged out" + ChessClient.help();
    }

    private String create(String... params) throws ResponseException {
        if (params.length == 1) {
            String gameName = params[0];
            int gameID = ChessClient.server.create(new CreateRequest(gameName), ChessClient.getAuthorization());
            updateGameDataMap();
            return String.format("Created game [%s] as game number [%s]", gameName, gameID);
        }
        throw new ResponseException(400, "Expected <name>");
    }

    private String list() throws ResponseException {
        updateGameDataMap();
        String message =    EscapeSequences.SET_TEXT_BOLD +
                            "\nList of games: \n" +
                            EscapeSequences.RESET_TEXT_BOLD_FAINT;
        String data = ChessClient.readGameDataMap();
        return message + ((data.isBlank()) ? "No current games" : data);
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
            if (incompatibleConvertToInt(params[0])) {
                String detail = EscapeSequences.SET_TEXT_ITALIC +
                                " ID must be a valid number" +
                                EscapeSequences.RESET_TEXT_ITALIC;
                throw new ResponseException(400, error + detail);
            }
            int gameID = Integer.parseInt(params[0]);
            String color = params[1].toLowerCase();
            if (!(Objects.equals(color, "white") || Objects.equals(color, "black"))) {
                String detail = EscapeSequences.SET_TEXT_ITALIC +
                                " Color must be \"white\" or \"black\"" +
                                EscapeSequences.RESET_TEXT_ITALIC;
                throw new ResponseException(400, error + detail);
            }
            ChessClient.server.join(new JoinRequest(color.toUpperCase(), gameID), ChessClient.getAuthorization());
            updateGameDataMap();
            ChessClient.state = State.GAMEPLAY;
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
            return String.format("Joined game [%s] as [%s]", gameID, color.toUpperCase()) + ChessClient.help();
        }
        throw new ResponseException(400, error);
    }

    private static boolean incompatibleConvertToInt(String s) {
        try {
            Integer.parseInt(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private static ChessGame.TeamColor getColor() {
        GameData gameData = ChessClient.getGameData(ChessClient.getCurrentGameID());
        String username = ChessClient.getUsername();
        if (Objects.equals(username, gameData.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(username, gameData.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

    private String observe(String... params) throws ResponseException {
        String error = "Expected <ID>";
        if (params.length == 1) {
            if (incompatibleConvertToInt(params[0])) {
                String detail = EscapeSequences.SET_TEXT_ITALIC +
                        " ID must be a valid number" +
                        EscapeSequences.RESET_TEXT_ITALIC;
                throw new ResponseException(400, error + detail);
            }
            int gameID = Integer.parseInt(params[0]);

            ChessClient.state = State.GAMEPLAY;
            ChessClient.setCurrentGameID(gameID);
            Gameplay.setState(GameplayState.OBSERVE);

            ChessBoard board = ChessClient.getGameData(gameID).game().getBoard();
            DrawChessBoard.drawBoard(board, System.out, GameplayState.OBSERVE);
            return String.format("Observing game [%s]", gameID) + ChessClient.help();
        }
        throw new ResponseException(400, error);
    }
}
