import server.Server;
import logger.LoggerManager;

import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger log = Logger.getLogger("serverLogger");
        LoggerManager.setup(log, "server.log");

        try {
            int port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
            Server server = new Server();
            server.run(port);
            log.info(String.format("Server started on port %d", port));

            Scanner scanner = new Scanner(System.in);
            String result = "";
            System.out.println("Enter \"quit\" to stop server");
            while (!Objects.equals(result, "quit")) {
                result = scanner.nextLine();
            }
            server.stop();
            log.info("Server stopped.");
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s", ex.getMessage());
        }
    }
}