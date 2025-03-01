package server;

import dataaccess.*;
import handler.RegisterHandler;
import service.ServiceManager;
import spark.*;

public class Server {
    // Memory data structures
    private final UserDAO userDataAccess = new MemoryUserDAO();
    private final AuthDAO authDataAccess = new MemoryAuthDAO();
    private final GameDAO gameDataAccess = new MemoryGameDAO();

    // Service Manager
    private final ServiceManager service = new ServiceManager(userDataAccess, authDataAccess, gameDataAccess);

    // Handlers
    private final RegisterHandler registerHandler = new RegisterHandler(service);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::handleRegister);
        Spark.delete("/db", this::handleClear);
        Spark.post("/session", this::handleLogin);
        Spark.delete("/session", this::handleLogout);
        Spark.get("/game", this::handleListGames);
        Spark.post("/game", this::handleCreateGame);
        Spark.put("/game", this::handleJoinGame);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object handleRegister(Request req, Response res) {
        return registerHandler.register(req, res);
    }

    private Object handleClear(Request req, Response res) {
        throw new RuntimeException("Not implemented");
    }

    private Object handleLogin(Request req, Response res) {
        throw new RuntimeException("Not implemented");
    }

    private Object handleLogout(Request req, Response res) {
        throw new RuntimeException("Not implemented");
    }

    private Object handleListGames(Request req, Response res) {
        throw new RuntimeException("Not implemented");
    }

    private Object handleCreateGame(Request req, Response res) {
        throw new RuntimeException("Not implemented");
    }

    private Object handleJoinGame(Request req, Response res) {
        throw new RuntimeException("Not implemented");
    }

    private static Object errorHandler(Exception e, Request req, Response res) {
        throw new RuntimeException("Not implemented");
    }
}
