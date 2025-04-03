package websocket.messages;

import model.GameData;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage{

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    GameData game;

    public GameData getGame() {
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
