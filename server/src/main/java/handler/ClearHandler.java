package handler;

import service.ServiceManager;
import exception.FailureResponse;
import spark.*;
import com.google.gson.Gson;

public class ClearHandler extends BaseHandler {
    public ClearHandler(ServiceManager service) {
        super(service);
    }

    public Object clear(Request req, Response res) {
        try {
            service.clear();

            res.type("application/json");
            res.status(200);

            return new Gson().toJson("");
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new FailureResponse("Error: " + e.getMessage()));
        }
    }
}
