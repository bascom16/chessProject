package websocket.messages;

public class NotificationMessage extends ServerMessage {
    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    String message;

    public String getMessage() {
        return message;
    }
}
