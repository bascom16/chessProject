package client;

import exception.ResponseException;
import handler.request.CreateRequest;
import handler.request.JoinRequest;
import handler.request.LoginRequest;
import handler.request.RegisterRequest;
import model.AuthData;
import model.GameData;
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
    public void registerMultipleSuccess() {
        RegisterRequest request1 = new RegisterRequest(username, password, email);
        AuthData data1 = assertDoesNotThrow( () -> facade.register(request1));
        assertEquals(username, data1.username());
        RegisterRequest request2 = new RegisterRequest("username2", "password2", "email2");
        AuthData data2 = assertDoesNotThrow( () -> facade.register(request2));
        assertEquals("username2", data2.username());
    }

    @Test
    public void registerBadRequest() {
        RegisterRequest request = new RegisterRequest(null, password, email);
        ResponseException error = assertThrows(ResponseException.class,
                () -> facade.register(request), "Bad register request should throw error");
        assertEquals(400, error.statusCode());
    }

    @Test
    public void registerUsernameTaken() {
        RegisterRequest request1 = new RegisterRequest(username, password, email);
        AuthData data1 = assertDoesNotThrow( () -> facade.register(request1));
        assertEquals(username, data1.username());
        RegisterRequest request2 = new RegisterRequest(username, "password2", "email2");
        assertThrows(ResponseException.class, () -> facade.register(request2), "Username taken");
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

    @Test
    public void createSuccess() {
        String authToken = register().authToken();
        CreateRequest request = new CreateRequest("game1");
        assertDoesNotThrow( () -> facade.create(request, authToken));
        GameData[] list = assertDoesNotThrow( () -> facade.list(authToken));
        assertEquals(1, list.length);
    }

    @Test
    public void createBadRequest() {
        String authToken = register().authToken();
        CreateRequest request = new CreateRequest(null);
        assertThrows(ResponseException.class,
                () -> facade.create(request, authToken), "Null gameName should cancel create");
        GameData[] list = assertDoesNotThrow( () -> facade.list(authToken));
        assertEquals(0, list.length);
    }

    @Test
    public void listSuccess() {
        String authToken = register().authToken();
        String game1 = "game1";
        CreateRequest request1 = new CreateRequest(game1);
        assertDoesNotThrow( () -> facade.create(request1, authToken));
        String game2 = "game2";
        CreateRequest request2 = new CreateRequest(game2);
        assertDoesNotThrow( () -> facade.create(request2, authToken));

        GameData[] list = assertDoesNotThrow( () -> facade.list(authToken));
        assertEquals(game1, list[0].gameName());
        assertEquals(game2, list[1].gameName());
        assertEquals(2, list.length);
    }

    @Test
    public void listUnauthorized() {
        assertThrows(ResponseException.class, () -> facade.list(null), "Unauthorized request");
    }

    @Test
    public void joinWhiteSuccess() {
        String authToken = register().authToken();
        String game = "gameName";
        int gameID = create(game, authToken);
        assertDoesNotThrow( () -> facade.join(new JoinRequest("WHITE", gameID), authToken));
        GameData[] list = assertDoesNotThrow( () -> facade.list(authToken));
        assertEquals(username, list[0].whiteUsername());
    }

    @Test
    public void joinBlackSuccess() {
        String authToken = register().authToken();
        String game = "gameName";
        int gameID = create(game, authToken);
        assertDoesNotThrow( () -> facade.join(new JoinRequest("BLACK", gameID), authToken));
        GameData[] list = assertDoesNotThrow( () -> facade.list(authToken));
        assertEquals(username, list[0].blackUsername());
    }

    @Test
    public void joinBadRequest() {
        String authToken = register().authToken();
        String game = "gameName";
        int gameID = create(game, authToken);
        assertThrows(ResponseException.class,
                () -> facade.join(new JoinRequest("NOT_A_COLOR", gameID), authToken),
                "Invalid color");
        GameData[] list = assertDoesNotThrow( () -> facade.list(authToken));
        assertNotEquals(username, list[0].whiteUsername());
        assertNotEquals(username, list[0].blackUsername());
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

    private int create(String gameName, String authToken) {
        return assertDoesNotThrow( () -> facade.create(new CreateRequest(gameName), authToken));
    }
}
