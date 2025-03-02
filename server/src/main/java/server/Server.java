package server;

import dataaccess.*;
import exception.ResponseException;
import handler.*;
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
    private final ClearHandler clearHandler = new ClearHandler(service);
    private final LoginHandler loginHandler = new LoginHandler(service);
    private final LogoutHandler logoutHandler = new LogoutHandler(service);
    private final ListHandler listHandler = new ListHandler(service);
    private final CreateHandler createHandler = new CreateHandler(service);
    private final JoinHandler joinHandler = new JoinHandler(service);

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
        Spark.exception(ResponseException.class, this::exceptionHandler);

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
        return clearHandler.clear(req, res);
    }

    private Object handleLogin(Request req, Response res) {
        return loginHandler.login(req, res);
    }

    private Object handleLogout(Request req, Response res) {
        return logoutHandler.logout(req, res);
    }

    private Object handleListGames(Request req, Response res) {
        return listHandler.list(req, res);
    }

    private Object handleCreateGame(Request req, Response res) {
        return createHandler.create(req, res);
    }

    private Object handleJoinGame(Request req, Response res) {
        return joinHandler.join(req, res);
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }
}
