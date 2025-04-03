import client.Repl;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            LoggerConfig.setup();
        } catch (IOException ex) {
            System.out.println("Logger uninitialized: " + ex.getMessage());
        }
        logger.info("Logger initialized");

        int port = 8080;
        String url = "http://localhost:" + port;

        System.out.println("♕ 240 Chess Client: ");

        new Repl(url).run();
    }
}