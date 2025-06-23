package com.shizuka.gateway.domain.exception;

import java.util.List;

public class RouteConfigNotFoundException extends RuntimeException {

    public RouteConfigNotFoundException(List<String> routeIds) {
        super("Error, the following IDs do not exist: " + routeIds);
    }

    public RouteConfigNotFoundException(String id) {
        super("Error, the following ID do not exist: " + id);
    }
}
