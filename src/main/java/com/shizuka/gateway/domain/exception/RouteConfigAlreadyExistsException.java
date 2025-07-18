package com.shizuka.gateway.domain.exception;

public class RouteConfigAlreadyExistsException extends RuntimeException {

  public RouteConfigAlreadyExistsException(String id) {
    super("Route config already exists with: " + id);
  }
}
