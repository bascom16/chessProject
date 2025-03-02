package handler;

import service.ServiceManager;
import spark.*;
import com.google.gson.Gson;

public class LoginHandler extends BaseHandler {
    public LoginHandler(ServiceManager service) {
        super(service);
    }

    public Object login(Request res, Response req) {
        throw new RuntimeException("Not implemented");
    }
}
