package com.shizuka.gateway.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shizuka.gateway.app.service.RouteConfigService;
import com.shizuka.gateway.domain.exception.RouteConfigNotFoundException;
import com.shizuka.gateway.domain.model.EnvEnum;
import com.shizuka.gateway.domain.model.RouteConfig;
import com.shizuka.gateway.domain.usecase.RouteConfigUseCase;
import com.shizuka.gateway.infra.factory.RouteDefinitionFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteConfigServiceImpl implements RouteConfigService {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final ApplicationEventPublisher publisher;
    private final RouteConfigUseCase routeConfigUseCase;
    private final RouteDefinitionFactory routeDefinitionFactory;

    @PostConstruct
    public void init() {
        list().forEach(this::registerRoute);
    }

    @Override
    public List<RouteConfig> list() {
        return routeConfigUseCase.getAll();
    }

    @Override
    public RouteConfig findById(String id) {
        return routeConfigUseCase.getById(id).orElseThrow(() -> new RouteConfigNotFoundException(id));
    }

    @Override
    public void create(RouteConfig config) {
        routeConfigUseCase.create(config);
        registerRoute(config);
        log.info("Route registered: {}", config.getId());
    }

    @Override
    public void update(RouteConfig config) {
        routeConfigUseCase.update(config);
        replaceRouteDefinition(config);
        log.info("Route updated: {}", config.getId());
    }

    @Override
    public void delete(String id) {
        routeConfigUseCase.delete(id);
        deleteRouteDefinition(id);
    }

    @Override
    public void deleteMany(List<String> ids) {
        routeConfigUseCase.deleteMany(ids);
        ids.forEach(this::deleteRouteDefinition);
    }

    @Override
    public void upsertMany(List<RouteConfig> configs) {
        routeConfigUseCase.upsertMany(configs);

        configs.forEach(config -> {
            if (routeDefinitionExists(config.getId())) {
                replaceRouteDefinition(config);
                log.info("Route updated via upsert: {}", config.getId());
            } else {
                registerRoute(config);
                log.info("Route created via upsert: {}", config.getId());
            }
        });
    }

    private boolean routeDefinitionExists(String routeId) {
        // Depende de tu forma de obtener rutas, por ahora usamos la lista del modelo
        return list().stream().anyMatch(cfg -> cfg.getId().equals(routeId));
    }


    @Override
    public void updateEnv(List<String> routeIds, EnvEnum newEnv) {
        routeConfigUseCase.updateEnv(routeIds, newEnv);

        List<RouteConfig> affectedRoutes = getAffectedRoutes(routeIds);

        affectedRoutes.forEach(this::replaceRouteDefinition);
    }

    @Override
    public Mono<Void> upsertFromFile(MultipartFile file) {
        return Mono.fromCallable(() -> {
            try (var inputStream = file.getInputStream()) {
                List<RouteConfig> configs = new ObjectMapper()
                        .readerFor(new TypeReference<List<RouteConfig>>() {})
                        .readValue(inputStream);

                if (configs == null || configs.isEmpty()) {
                    throw new IllegalArgumentException("Uploaded file contains no route configurations.");
                }

                upsertMany(configs);

                return true;
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to read uploaded file: " + e.getMessage(), e);
            }
        }).then();
    }

    private List<RouteConfig> getAffectedRoutes(List<String> routeIds) {
        return list().stream()
                .filter(cfg -> routeIds == null || routeIds.isEmpty() || routeIds.contains(cfg.getId()))
                .toList();
    }

    private void replaceRouteDefinition(RouteConfig routeConfig) {
        routeDefinitionWriter.delete(Mono.just(routeConfig.getId()))
                .onErrorResume(e -> Mono.empty())
                .then(Mono.fromRunnable(() -> registerRoute(routeConfig)))
                .subscribe();
    }

    private void registerRoute(RouteConfig routeConfig) {
        RouteDefinition definition = routeDefinitionFactory.from(routeConfig);
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    private void deleteRouteDefinition(String id) {
        routeDefinitionWriter.delete(Mono.just(id))
                .onErrorResume(e -> Mono.empty())
                .doOnSuccess(unused -> log.info("Route deleted: {}", id))
                .subscribe();
    }
}
