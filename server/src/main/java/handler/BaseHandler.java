package handler;

import service.ServiceManager;

public class BaseHandler {
    protected final ServiceManager service;

    public BaseHandler(ServiceManager service) {
        this.service = service;
    }
}
