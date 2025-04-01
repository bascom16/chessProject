package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage{

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    ChessGame game;

    public ChessGame getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoadGameMessage that)) {
            return false;
        }
        return getGame() == that.getGame();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getGame());
    }
}
