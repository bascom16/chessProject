package server;

import dataaccess.*;
import service.ServiceManager;
import spark.*;

public class Server {

    private final UserDAO userDataAccess = new MemoryUserDAO();
    private final AuthDAO authDataAccess = new MemoryAuthDAO();
    private final GameDAO gameDataAccess = new MemoryGameDAO();

    public final ServiceManager service = new ServiceManager(userDataAccess, authDataAccess, gameDataAccess);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
