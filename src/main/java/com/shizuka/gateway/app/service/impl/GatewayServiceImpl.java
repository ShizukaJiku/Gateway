package com.shizuka.gateway.app.service.impl;

import com.shizuka.gateway.app.service.GatewayService;
import com.shizuka.gateway.domain.exception.RouteConfigNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GatewayServiceImpl implements GatewayService {

  private final RouteDefinitionLocator routeDefinitionLocator;

  @Override
  public Flux<RouteDefinition> getRouteDefinitions() {
    return routeDefinitionLocator.getRouteDefinitions();
  }

  @Override
  public Mono<RouteDefinition> getRouteDefinitionByIndex(int index) {
    return routeDefinitionLocator.getRouteDefinitions().elementAt(index)
        .doOnError(throwable -> {
          throw new RouteConfigNotFoundException(String.valueOf(index));
        });
  }
}
