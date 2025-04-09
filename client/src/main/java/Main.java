import client.Repl;
import Logger.LoggerManager;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {

        int port = 8080;
        String url = "http://localhost:" + port;

        System.out.println("♕ 240 Chess Client: ");

        new Repl(url).run();
    }
}