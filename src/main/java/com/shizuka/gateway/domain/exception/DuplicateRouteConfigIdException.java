package com.shizuka.gateway.domain.exception;

import java.util.List;

public class DuplicateRouteConfigIdException extends RuntimeException {

    public DuplicateRouteConfigIdException(List<String> duplicatedIds) {
        super("Duplicate route IDs in request: " + duplicatedIds);
    }
}
