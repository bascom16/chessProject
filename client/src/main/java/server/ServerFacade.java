package server;

import com.google.gson.Gson;
import exception.ClientException;
import handler.request.CreateRequest;
import handler.request.JoinRequest;
import handler.request.LoginRequest;
import handler.request.RegisterRequest;
import handler.result.CreateResult;
import model.AuthData;
import model.GameData;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String url) {
        serverURL = url;
    }

//    Passes username, password, email in body
//    Returns username and authToken
    public AuthData register(RegisterRequest request) throws ClientException {
        String path = "/user";
        return this.makeRequest("POST", path, request, null, AuthData.class);
    }

//    passes username and password in body
//    Returns username and authToken
    public AuthData login(LoginRequest request) throws ClientException {
        String path = "/session";
        return this.makeRequest("POST", path, request, null, AuthData.class);
    }

//    passes authToken in header
    public void logout(String authToken) throws ClientException {
        String path = "/session";
        this.makeRequest("DELETE", path, null, authToken, null);
    }

//    passes authToken in header
//    Returns list of gameData
    public GameData[] list(String authToken) throws ClientException {
        String path = "/game";
        record GameList(GameData[] games) {}
        GameList response = this.makeRequest("GET", path, null, authToken, GameList.class);
        return response.games();
    }

//    passes authToken in header
//    passes gameName in body
//    Returns gameID
    public Integer create(CreateRequest request, String authToken) throws ClientException {
        String path = "/game";
        return this.makeRequest("POST", path, request, authToken, CreateResult.class).gameID();
    }

//    passes authToken in header
//    passes playerColor, gameID in body
    public void join(JoinRequest request, String authToken) throws ClientException {
        String path = "/game";
        this.makeRequest("PUT", path, request, authToken, null);
    }

    public void clear() throws ClientException {
        String path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass)
            throws ClientException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeHeader(authToken, http);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ClientException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String requestData = new Gson().toJson(request);
            try (OutputStream requestBody = http.getOutputStream()) {
                requestBody.write(requestData.getBytes());
            }
        }
    }

    private static void writeHeader(String authToken, HttpURLConnection http) {
        if (authToken != null) {
            http.setRequestProperty("Authorization", authToken);
            http.setRequestProperty("Accept", "application/json");
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ClientException {
        int status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream responseError = http.getErrorStream()) {
                if (responseError != null) {
                    throw ClientException.fromJson(responseError);
                }
            }
            throw new ClientException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
