package client;

import exception.ResponseException;

public interface ClientStateInterface {

    String help();

    String eval(String cmd, String... params) throws ResponseException;
}
