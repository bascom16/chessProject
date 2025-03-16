package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.FailureResponse;
import exception.ResponseException;
import service.ServiceManager;
import spark.*;

import java.util.Map;

public class BaseHandler {
    protected final ServiceManager service;

    public BaseHandler(ServiceManager service) {
        this.service = service;
    }

    protected Object handleResponseException(Response res, ResponseException ex) {
        res.status(ex.statusCode());
        FailureResponse response = new FailureResponse("Error: " + ex.getMessage(), ex.statusCode());
        return new Gson().toJson(Map.of("message", response.message(), "status", ex.statusCode()));
    }

    protected Object handleDataAccessException(Response res, DataAccessException ex) {
        int status = 500;
        res.status(status);
        FailureResponse response = new FailureResponse("Error: " + ex.getMessage(), status);
        return new Gson().toJson(Map.of("message", response.message(), "status", response.status()));
    }
}
