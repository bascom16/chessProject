package client;

import exception.ResponseException;
import handler.request.LoginRequest;
import handler.request.RegisterRequest;
import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    private static final String username = "username";
    private static final String password = "password";
    private static final String email = "email";


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = "http://localhost:" + server.port();
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() {
        assertDoesNotThrow( () -> facade.clear());
    }

    @Test
    public void registerSuccess() {
        RegisterRequest request = new RegisterRequest(username, password, email);
        AuthData data = assertDoesNotThrow( () -> facade.register(request));
        assertEquals(username, data.username());
    }

    @Test
    public void registerBadRequest() {
        RegisterRequest request = new RegisterRequest(null, password, email);
        ResponseException error = assertThrows(ResponseException.class,
                () -> facade.register(request), "Bad register request should throw error");
        assertEquals(400, error.statusCode());
    }

    @Test
    public void loginSuccess() {
        String authToken = register().authToken();
        logout(authToken);

        LoginRequest request = new LoginRequest(username, password);
        assertDoesNotThrow( () -> facade.login(request));
    }

    @Test
    public void unauthorizedLogin() {
        String authToken = register().authToken();
        logout(authToken);

        LoginRequest request = new LoginRequest(username, "the wrong password");
        ResponseException error = assertThrows(ResponseException.class,
                () -> facade.login(request), "Wrong password should invalidate login");
        assertEquals(401, error.statusCode());
    }

    @Test
    public void logoutSuccess() {
        String authToken = register().authToken();
        assertDoesNotThrow( () -> facade.logout(authToken));
    }

    @Test
    public void unauthorizedLogout() {
        register();
        ResponseException error = assertThrows(ResponseException.class,
                () -> facade.logout("the wrong authorization"),
                "Wrong authToken should invalidate Logout");
        assertEquals(401, error.statusCode());
    }

    private AuthData register(String username, String password, String email) {
        return assertDoesNotThrow( () -> facade.register(new RegisterRequest(username, password, email)));
    }

    private AuthData register() {
        return register(username, password, email);
    }

    private void logout(String authToken) {
        assertDoesNotThrow( () -> facade.logout(authToken));
    }
}
