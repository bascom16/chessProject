package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;

public class LoadGameMessage extends ServerMessage{

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    ChessGame game;

    public ChessGame getGame() {
        return game;
    }
}
