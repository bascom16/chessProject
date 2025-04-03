import client.Repl;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger log = Logger.getLogger("clientLogger");
        try {
            LoggerManager.setup(log, "client.log");
        } catch (IOException ex) {
            System.out.println("Logger uninitialized: " + ex.getMessage());
        }
        log.info("Client Logger initialized");

        int port = 8080;
        String url = "http://localhost:" + port;

        System.out.println("♕ 240 Chess Client: ");

        new Repl(url).run();
    }
}