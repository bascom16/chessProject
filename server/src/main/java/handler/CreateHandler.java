package handler;

import exception.ResponseException;
import handler.request.AuthorizationRequest;
import handler.request.CreateRequest;
import handler.result.CreateResult;
import service.ServiceManager;
import spark.*;
import com.google.gson.Gson;

public class CreateHandler extends BaseHandler {
    public CreateHandler(ServiceManager service) {
        super(service);
    }

    public Object create(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null) {
                throw new ResponseException(401, "unauthorized");
            }
            AuthorizationRequest authRequest = new AuthorizationRequest(authToken);
            Gson gson = new Gson();
            CreateRequest createRequest = gson.fromJson(req.body(), CreateRequest.class);
            if (createRequest.gameName() == null) {
                throw new ResponseException(400, "bad request");
            }

            res.type("application/json");
            res.status(200); // Success
            CreateResult result = service.create(authRequest, createRequest);
            return gson.toJson(result);
        } catch (ResponseException ex) {
            return handleResponseException(res, ex);
        }
    }
}
