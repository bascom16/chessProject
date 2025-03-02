package handler;

import com.google.gson.Gson;
import exception.FailureResponse;
import exception.ResponseException;
import model.AuthData;
import service.ServiceManager;
import spark.*;
import com.google.gson.Gson;


import java.util.Map;

public class BaseHandler {
    protected final ServiceManager service;

    public BaseHandler(ServiceManager service) {
        this.service = service;
    }

    protected Object handleResponseException(Response res, ResponseException ex) {
        res.status(ex.StatusCode());
        FailureResponse response = new FailureResponse("Error: " + ex.getMessage());
        return new Gson().toJson(Map.of("message", response.message()));
    }
}
