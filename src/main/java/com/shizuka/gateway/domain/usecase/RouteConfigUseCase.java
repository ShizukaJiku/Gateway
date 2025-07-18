package com.shizuka.gateway.domain.usecase;

import com.shizuka.gateway.domain.exception.DuplicateRouteConfigIdException;
import com.shizuka.gateway.domain.exception.RouteConfigAlreadyExistsException;
import com.shizuka.gateway.domain.exception.RouteConfigNotFoundException;
import com.shizuka.gateway.domain.model.EnvEnum;
import com.shizuka.gateway.domain.model.RouteConfig;
import com.shizuka.gateway.domain.provider.RouteConfigProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteConfigUseCase {

  private final RouteConfigProvider routeConfigProvider;

  public List<RouteConfig> getAll() {
    return routeConfigProvider.getAll();
  }

  public Optional<RouteConfig> getById(String id) {
    return routeConfigProvider.getById(id);
  }

  public void create(RouteConfig config) {
    assertNotExists(config.getId());
    log.info("Creating route config: {}", config.getId());
    routeConfigProvider.create(config);
  }

  public void update(RouteConfig config) {
    assertExists(config.getId());
    log.info("Updating route config: {}", config.getId());
    routeConfigProvider.update(config);
  }

  public void delete(String id) {
    assertExists(id);
    log.info("Deleting route config: {}", id);
    routeConfigProvider.delete(id);
  }

  public void deleteMany(List<String> ids) {
    assertAllExist(ids);
    log.info("Deleting {} route configs", ids.size());
    routeConfigProvider.deleteMany(ids);
  }

  public void upsertMany(List<RouteConfig> configs) {
    Set<String> seen = new HashSet<>();
    List<String> duplicates = new ArrayList<>();
    List<RouteConfig> toUpdate = new ArrayList<>();
    List<RouteConfig> toCreate = new ArrayList<>();

    for (RouteConfig config : configs) {
      if (!seen.add(config.getId())) {
        duplicates.add(config.getId());
        continue;
      }

      if (routeConfigProvider.exists(config.getId())) {
        toUpdate.add(config);
      } else {
        toCreate.add(config);
      }
    }

    if (!duplicates.isEmpty()) {
      throw new DuplicateRouteConfigIdException(duplicates);
    }

    if (!toUpdate.isEmpty()) {
      log.info("Upserting {} configs: will update {}", configs.size(), toUpdate.size());
      routeConfigProvider.updateMany(toUpdate);
    }
    if (!toCreate.isEmpty()) {
      log.info("Upserting {} configs: will create {}", configs.size(), toCreate.size());
      routeConfigProvider.createMany(toCreate);
    }
  }

  public void updateEnv(List<String> routeIds, EnvEnum newEnv) {
    List<RouteConfig> all = getAll();
    List<RouteConfig> toUpdate;

    if (routeIds == null || routeIds.isEmpty()) {
      toUpdate = all;
    } else {
      Set<String> knownIds = all.stream().map(RouteConfig::getId).collect(Collectors.toSet());

      List<String> missing = routeIds.stream()
          .filter(id -> !knownIds.contains(id))
          .toList();

      if (!missing.isEmpty()) {
        throw new RouteConfigNotFoundException(missing);
      }

      toUpdate = all.stream()
          .filter(cfg -> routeIds.contains(cfg.getId()))
          .toList();
    }

    toUpdate.forEach(cfg -> cfg.setCurrentEnv(newEnv));
    routeConfigProvider.updateMany(toUpdate);
    log.info("Updated environment to {} for {} route(s)", newEnv, toUpdate.size());
  }

  // === Private Validation Helpers ===

  private void assertExists(String id) {
    if (!routeConfigProvider.exists(id)) {
      throw new RouteConfigNotFoundException(id);
    }
  }

  private void assertNotExists(String id) {
    if (routeConfigProvider.exists(id)) {
      throw new RouteConfigAlreadyExistsException(id);
    }
  }

  private void assertAllExist(List<String> ids) {
    List<String> notFound = ids.stream()
        .filter(id -> !routeConfigProvider.exists(id))
        .toList();

    if (!notFound.isEmpty()) {
      throw new RouteConfigNotFoundException("The following IDs were not found: " + notFound);
    }
  }
}
