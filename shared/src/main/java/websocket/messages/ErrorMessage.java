package websocket.messages;

public class ErrorMessage extends ServerMessage {
    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    String message;

    public String getMessage() {
        return message;
    }
}
