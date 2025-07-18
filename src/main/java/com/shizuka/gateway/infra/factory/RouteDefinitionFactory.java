package com.shizuka.gateway.infra.factory;

import com.shizuka.gateway.domain.model.RouteConfig;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RouteDefinitionFactory {

  public RouteDefinition from(RouteConfig config) {
    var envConfig = config.getCurrentEnvConfig();

    RouteDefinition route = new RouteDefinition();
    route.setId(config.getId());
    route.setUri(URI.create(envConfig.getUri()));

    // Predicado Path
    route.setPredicates(List.of(
        new PredicateDefinition("Path=" + config.getPathPattern())
    ));

    // Filtros
    List<FilterDefinition> filters = new ArrayList<>();

    // Rewrite
    filters.add(new FilterDefinition("RewritePath=" + config.getRewriteRegex() + ", " + config.getRewriteReplacement()));

    // Filtros personalizados
    if (config.getFilters() != null) {
      config.getFilters().stream()
          .map(FilterDefinition::new)
          .forEach(filters::add);
    }

    // Headers por entorno
    if (envConfig.getHeaders() != null) {
      envConfig.getHeaders().forEach((k, v) -> filters.add(
          new FilterDefinition("AddRequestHeader=" + k + "," + v)
      ));
    }

    route.setFilters(filters);
    return route;
  }
}
