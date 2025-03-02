package handler;

import exception.FailureResponse;
import exception.ResponseException;
import handler.request.LoginRequest;
import handler.result.LoginResult;
import service.ServiceManager;
import spark.*;
import com.google.gson.Gson;

import java.util.Map;

public class LoginHandler extends BaseHandler {
    public LoginHandler(ServiceManager service) {
        super(service);
    }

    public Object login(Request req, Response res) {
        try {
            Gson gson = new Gson();
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
            if (loginRequest.username() == null || loginRequest.password() == null) {
                throw new ResponseException(401, "unauthorized");
            }
            res.type("application/json");
            res.status(200); // Success
            LoginResult loginResult = service.login(loginRequest);
            return gson.toJson(Map.of("username", loginResult.username(),"authToken", loginResult.authToken()));
        } catch (ResponseException ex) {
            return handleResponseException(res, ex);
        }
    }
}
