package com.shizuka.gateway.domain.provider;

import com.shizuka.gateway.domain.model.RouteConfig;

import java.util.List;
import java.util.Optional;

public interface RouteConfigProvider {

    List<RouteConfig> getAll();

    Optional<RouteConfig> getById(String id);

    boolean exists(String id);

    void create(RouteConfig config);

    void createMany(List<RouteConfig> configs);

    void update(RouteConfig config);

    void updateMany(List<RouteConfig> configs);

    void delete(String id);

    void deleteMany(List<String> ids);
}
