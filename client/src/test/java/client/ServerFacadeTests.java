package client;

import exception.ClientException;
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

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";


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
        RegisterRequest request = new RegisterRequest(USERNAME, PASSWORD, EMAIL);
        AuthData data = assertDoesNotThrow( () -> facade.register(request));
        assertEquals(USERNAME, data.username());
    }

    @Test
    public void registerMultipleSuccess() {
        assertEquals(USERNAME, register().username());
        String user2 = "username2";
        assertEquals(user2, register(user2, "password2", "email2").username());
    }

    @Test
    public void registerBadRequest() {
        RegisterRequest request = new RegisterRequest(null, PASSWORD, EMAIL);
        assertThrows(ClientException.class,
                () -> facade.register(request), "Bad register request should throw error");
    }

    @Test
    public void registerUsernameTaken() {
        RegisterRequest request1 = new RegisterRequest(USERNAME, PASSWORD, EMAIL);
        AuthData data1 = assertDoesNotThrow( () -> facade.register(request1));
        assertEquals(USERNAME, data1.username());
        RegisterRequest request2 = new RegisterRequest(USERNAME, "password2", "email2");
        assertThrows(ClientException.class, () -> facade.register(request2), "Username taken");
    }

    @Test
    public void loginSuccess() {
        String authToken = register().authToken();
        logout(authToken);

        LoginRequest request = new LoginRequest(USERNAME, PASSWORD);
        assertDoesNotThrow( () -> facade.login(request));
    }

    @Test
    public void unauthorizedLogin() {
        String authToken = register().authToken();
        logout(authToken);

        LoginRequest request = new LoginRequest(USERNAME, "the wrong password");
        assertThrows(ClientException.class,
                () -> facade.login(request), "Wrong password should invalidate login");
    }

    @Test
    public void logoutSuccess() {
        String authToken = register().authToken();
        assertDoesNotThrow( () -> facade.logout(authToken));
    }

    @Test
    public void unauthorizedLogout() {
        register();
        assertThrows(ClientException.class,
                () -> facade.logout("the wrong authorization"),
                "Wrong authToken should invalidate Logout");
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
        assertThrows(ClientException.class,
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
        assertThrows(ClientException.class, () -> facade.list(null), "Unauthorized request");
    }

    @Test
    public void joinWhiteSuccess() {
        String authToken = register().authToken();
        String game = "gameName";
        int gameID = create(game, authToken);
        assertDoesNotThrow( () -> facade.join(new JoinRequest("WHITE", gameID), authToken));
        GameData[] list = assertDoesNotThrow( () -> facade.list(authToken));
        assertEquals(USERNAME, list[0].whiteUsername());
    }

    @Test
    public void joinBlackSuccess() {
        String authToken = register().authToken();
        String game = "gameName";
        int gameID = create(game, authToken);
        assertDoesNotThrow( () -> facade.join(new JoinRequest("BLACK", gameID), authToken));
        GameData[] list = assertDoesNotThrow( () -> facade.list(authToken));
        assertEquals(USERNAME, list[0].blackUsername());
    }

    @Test
    public void joinBadRequest() {
        String authToken = register().authToken();
        String game = "gameName";
        int gameID = create(game, authToken);
        assertThrows(ClientException.class,
                () -> facade.join(new JoinRequest("NOT_A_COLOR", gameID), authToken),
                "Invalid color");
        GameData[] list = assertDoesNotThrow( () -> facade.list(authToken));
        assertNotEquals(USERNAME, list[0].whiteUsername());
        assertNotEquals(USERNAME, list[0].blackUsername());
    }

    private AuthData register(String username, String password, String email) {
        return assertDoesNotThrow( () -> facade.register(new RegisterRequest(username, password, email)));
    }

    private AuthData register() {
        return register(USERNAME, PASSWORD, EMAIL);
    }

    private void logout(String authToken) {
        assertDoesNotThrow( () -> facade.logout(authToken));
    }

    private int create(String gameName, String authToken) {
        return assertDoesNotThrow( () -> facade.create(new CreateRequest(gameName), authToken));
    }
}
