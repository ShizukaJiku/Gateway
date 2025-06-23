package com.shizuka.gateway.app.service;

import com.shizuka.gateway.domain.model.EnvEnum;
import com.shizuka.gateway.domain.model.RouteConfig;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RouteConfigService {

    List<RouteConfig> list();

    RouteConfig findById(String id);

    void create(RouteConfig config);

    void update(RouteConfig config);

    void delete(String id);

    void deleteMany(List<String> ids);

    void upsertMany(List<RouteConfig> configs);

    void updateEnv(List<String> routeIds, EnvEnum newEnv);

    Mono<Void> upsertFromFile(MultipartFile file);
}
