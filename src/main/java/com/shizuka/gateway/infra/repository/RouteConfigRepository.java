package com.shizuka.gateway.infra.repository;

import com.shizuka.gateway.domain.model.RouteConfig;
import java.util.List;
import java.util.Optional;

public interface RouteConfigRepository {
  List<RouteConfig> getAll();

  Optional<RouteConfig> getById(String id);

  void create(RouteConfig config);

  void update(RouteConfig config);

  void delete(String id);

  boolean exists(String id);

  void createMany(List<RouteConfig> configs);

  void updateMany(List<RouteConfig> configs);

  void deleteMany(List<String> ids);
}
