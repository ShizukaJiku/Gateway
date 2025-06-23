package com.shizuka.gateway.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class GatewayRouteController {

    private final RouteDefinitionLocator routeDefinitionLocator;

    @GetMapping("/gateway")
    public Flux<RouteDefinition> getRouteDefinitions() {
        return routeDefinitionLocator.getRouteDefinitions();
    }
}
