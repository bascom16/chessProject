package handler;

import handler.request.RegisterRequest;
import handler.result.RegisterResult;
import service.ServiceManager;
import spark.*;
import com.google.gson.Gson;

public class RegisterHandler extends BaseHandler {
    public RegisterHandler(ServiceManager service) {
        super(service);
    }

    public Object register(Request req, Response res) {
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
        RegisterResult registerResult = service.register(registerRequest);
        return gson.toJson(registerResult);
    }
}
