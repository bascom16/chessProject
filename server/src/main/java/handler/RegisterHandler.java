package handler;

import dataaccess.DataAccessException;
import exception.ResponseException;
import handler.request.RegisterRequest;
import handler.result.RegisterResult;
import exception.FailureResponse;
import service.ServiceManager;
import spark.*;
import com.google.gson.Gson;

public class RegisterHandler extends BaseHandler {
    public RegisterHandler(ServiceManager service) {
        super(service);
    }

    public Object register(Request req, Response res) {
        try {
            Gson gson = new Gson();
            RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
            if (    registerRequest.username() == null ||
                    registerRequest.password() == null ||
                    registerRequest.email() == null) {
                res.status(400); // Bad Request
                return gson.toJson(new FailureResponse("Error: bad request"));
            }

            res.type("application/json");
            res.status(200); // Success
            RegisterResult registerResult = service.register(registerRequest);

            return gson.toJson(registerResult);
        } catch (ResponseException ex) {
            return handleResponseException(res, ex);
        } catch (DataAccessException ex) {
            return handleDataAccessException(res, ex);
        }
    }
}
