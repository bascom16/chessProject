package handler;

import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import exception.ResponseException;
import handler.request.AuthorizationRequest;
import service.ServiceManager;
import spark.*;

public class LogoutHandler extends BaseHandler {
    public LogoutHandler(ServiceManager service) {
        super(service);
    }

    public Object logout(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            AuthorizationRequest logoutRequest = new AuthorizationRequest(authToken);
            if (logoutRequest.authToken() == null) {
                throw new ResponseException(401, "unauthorized");
            }

            service.logout(logoutRequest);

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
