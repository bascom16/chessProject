package server;

import dataaccess.*;
import exception.ResponseException;
import handler.*;
import server.websocket.WebSocketHandler;
import service.ServiceManager;
import spark.*;
import logger.LoggerManager;
import java.util.logging.Logger;

public class Server {
    Logger log = Logger.getLogger("serverLogger");

    // WebSocket
    private final WebSocketHandler webSocketHandler;

    // Memory data structures
    private UserDAO userDataAccess;
    private AuthDAO authDataAccess;
    private GameDAO gameDataAccess;

    // Handlers
    private final RegisterHandler registerHandler;
    private final ClearHandler clearHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final ListHandler listHandler;
    private final CreateHandler createHandler;
    private final JoinHandler joinHandler;

    public Server() {
        LoggerManager.setup(log, "server.log");

        // Memory data structures
        try {
            userDataAccess = new MySQLUserDAO();
            authDataAccess = new MySQLAuthDAO();
            gameDataAccess = new MySQLGameDAO();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        // Websocket
        webSocketHandler = new WebSocketHandler(authDataAccess, gameDataAccess);
        // Service Manager
        ServiceManager service = new ServiceManager(userDataAccess, authDataAccess, gameDataAccess);

        // Handlers
        registerHandler = new RegisterHandler(service);
        clearHandler = new ClearHandler(service);
        loginHandler = new LoginHandler(service);
        logoutHandler = new LogoutHandler(service);
        listHandler = new ListHandler(service);
        createHandler = new CreateHandler(service);
        joinHandler = new JoinHandler(service);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        try {
            userDataAccess = new MySQLUserDAO();
            authDataAccess = new MySQLAuthDAO();
            gameDataAccess = new MySQLGameDAO();
        } catch (Exception ex) {
            log.severe("MySQL database not initialized");
        }

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
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    public int port() {
        return Spark.port();
    }
}
