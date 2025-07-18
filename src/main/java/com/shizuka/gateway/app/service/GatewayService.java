package com.shizuka.gateway.app.service;

import org.springframework.cloud.gateway.route.RouteDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GatewayService {
  Flux<RouteDefinition> getRouteDefinitions();

  Mono<RouteDefinition> getRouteDefinitionByIndex(int index);
}
