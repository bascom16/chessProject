package websocket.messages;

import java.util.Objects;

public class ErrorMessage extends ServerMessage {
    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    String message;

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ErrorMessage that)) {
            return false;
        }
        return Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getMessage());
    }
}
