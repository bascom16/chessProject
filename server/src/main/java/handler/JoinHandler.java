package handler;

import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import exception.ResponseException;
import handler.request.AuthorizationRequest;
import handler.request.JoinRequest;
import service.ServiceManager;
import spark.*;
import com.google.gson.Gson;

import java.util.Objects;

public class JoinHandler extends BaseHandler {
    public JoinHandler(ServiceManager service) {
        super(service);
    }

    public Object join(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null) {
                throw new ResponseException(401, "unauthorized");
            }
            AuthorizationRequest authRequest = new AuthorizationRequest(authToken);
            Gson gson = new Gson();
            JoinRequest joinRequest = gson.fromJson(req.body(), JoinRequest.class);
            if (    !Objects.equals(joinRequest.playerColor(), "WHITE") &&
                    !Objects.equals(joinRequest.playerColor(), "BLACK") ||
                    joinRequest.gameID() == null) {
                throw new ResponseException(400, "bad request");
            }

            service.join(authRequest, joinRequest);

            res.type("application/json");
            res.status(200); // Success
            return new JsonObject();
        } catch (ResponseException ex) {
            return handleResponseException(res, ex);
        } catch (DataAccessException ex) {
            return handleDataAccessException(res, ex);
        }
    }
}
