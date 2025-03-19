package client;

import exception.ClientException;

public interface ClientStateInterface {

    String help();

    String eval(String cmd, String... params) throws ClientException;
}
