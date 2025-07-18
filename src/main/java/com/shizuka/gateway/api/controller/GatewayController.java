package com.shizuka.gateway.api.controller;

import com.shizuka.gateway.app.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class GatewayController {

  private final GatewayService gatewayService;

  @GetMapping
  public Flux<RouteDefinition> getRouteDefinitions() {
    return gatewayService.getRouteDefinitions();
  }

  @GetMapping("/{index}")
  public Mono<RouteDefinition> getRouteDefinitionByIndex(@PathVariable int index) {
    return gatewayService.getRouteDefinitionByIndex(index);
  }
}
