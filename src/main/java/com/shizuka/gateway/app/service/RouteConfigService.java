package com.shizuka.gateway.app.service;

import com.shizuka.gateway.domain.model.EnvEnum;
import com.shizuka.gateway.domain.model.RouteConfig;
import java.util.List;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;

public interface RouteConfigService {

  List<RouteConfig> list();

  RouteConfig findById(String id);

  void create(RouteConfig config);

  void update(RouteConfig config, String id);

  void delete(String id);

  void deleteMany(List<String> ids);

  void upsertMany(List<RouteConfig> configs);

  void updateEnv(List<String> routeIds, EnvEnum newEnv);

  Flux<Void> upsertFromFile(FilePart routes);
}
