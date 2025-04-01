package websocket.messages;

import java.util.Objects;

public class NotificationMessage extends ServerMessage {
    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
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
        if (!(o instanceof NotificationMessage that)) {
            return false;
        }
        return Objects.equals(message, that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getMessage());
    }
}
