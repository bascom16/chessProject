package websocket.messages;

import java.util.Objects;

public class ErrorMessage extends ServerMessage {
    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ErrorMessage that)) {
            return false;
        }
        return Objects.equals(getErrorMessage(), that.getErrorMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getErrorMessage());
    }
}
