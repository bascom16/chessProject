package handler;

import handler.request.RegisterRequest;
import handler.result.RegisterResult;
import exception.FailureResponse;
import service.ServiceManager;
import spark.*;
import com.google.gson.Gson;

import java.util.Map;

public class RegisterHandler extends BaseHandler {
    public RegisterHandler(ServiceManager service) {
        super(service);
    }

    public Object register(Request req, Response res) {
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            res.status(400); // Bad Request
            return gson.toJson(new FailureResponse("Invalid input"));
        }

        res.status(200); // Success
        RegisterResult registerResult = service.register(registerRequest);

        return gson.toJson(Map.of("username", registerResult.username(),"authToken", registerResult.authToken()));
    }
}
