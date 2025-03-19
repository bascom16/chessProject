import exception.ResponseException;

public interface ClientState {

    String help();

    String eval(String cmd, String... params) throws ResponseException;
}
