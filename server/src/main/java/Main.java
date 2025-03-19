import server.Server;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            int port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
            Server server = new Server();
            server.run(port);
            System.out.printf("Server started on port %d%n\n", port);

            Scanner scanner = new Scanner(System.in);
            String result = "";
            System.out.println("Enter \"quit\" to stop server");
            while (!Objects.equals(result, "quit")) {
                result = scanner.nextLine();
            }
            server.stop();
            System.out.println("Stopped Server.");
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

    }
}