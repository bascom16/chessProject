import exception.ResponseException;

public interface ClientState {

    public String help();

    public String eval(String cmd, String... params) throws ResponseException;
}
