package handler;

import exception.ResponseException;
import handler.request.AuthorizationRequest;
import handler.result.ListResult;
import model.GameData;
import service.ServiceManager;
import spark.*;
import com.google.gson.Gson;

import java.util.Map;

public class ListHandler extends BaseHandler {
    public ListHandler(ServiceManager service) {
        super(service);
    }

    public Object list(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            AuthorizationRequest authRequest = new AuthorizationRequest(authToken);
            if (authRequest.authToken() == null) {
                throw new ResponseException(401, "unauthorized");
            }

            res.type("application/json");
            res.status(200); // Success
            ListResult result = service.list(authRequest);
            GameData[] list = result.gameDataCollection().toArray(new GameData[0]);
            return new Gson().toJson(Map.of("games", list));
        } catch (ResponseException ex) {
            return handleResponseException(res, ex);
        }
    }
}
