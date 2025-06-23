package com.shizuka.gateway.infra.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shizuka.gateway.domain.model.RouteConfig;
import com.shizuka.gateway.infra.repository.RouteConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Repository
public class RouteConfigJsonRepository implements RouteConfigRepository {

    private final File jsonFile = new File("config/routes.json");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<RouteConfig> cache = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        List<RouteConfig> loaded = loadRoutesFromJson();
        cache.clear();
        cache.addAll(loaded);
    }

    @Override
    public List<RouteConfig> getAll() {
        return Collections.unmodifiableList(cache);
    }

    @Override
    public Optional<RouteConfig> getById(String id) {
        return cache.stream()
                .filter(cfg -> cfg.getId().equals(id))
                .findFirst();
    }

    @Override
    public void create(RouteConfig config) {
        if (exists(config.getId())) {
            throw new IllegalStateException("Ya existe una ruta con el ID: " + config.getId());
        }
        cache.add(config);
        persist();
    }

    @Override
    public void update(RouteConfig config) {
        if (!replaceById(config)) {
            throw new IllegalStateException("No existe una ruta con el ID: " + config.getId());
        }
        persist();
    }

    @Override
    public void createMany(List<RouteConfig> configs) {
        List<String> errors = new ArrayList<>();
        for (RouteConfig config : configs) {
            if (exists(config.getId())) {
                errors.add(config.getId());
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalStateException("Ya existen rutas con los IDs: " + errors);
        }

        cache.addAll(configs);
        persist();
    }

    @Override
    public void updateMany(List<RouteConfig> configs) {
        List<String> errors = new ArrayList<>();
        for (RouteConfig updated : configs) {
            if (!replaceById(updated)) {
                errors.add(updated.getId());
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalStateException("No se encontraron rutas con los IDs: " + errors);
        }
        persist();
    }

    @Override
    public void deleteMany(List<String> ids) {
        boolean removed = cache.removeIf(cfg -> ids.contains(cfg.getId()));
        if (removed) {
            persist();
        }
    }

    @Override
    public void delete(String id) {
        boolean removed = cache.removeIf(cfg -> cfg.getId().equals(id));
        if (removed) {
            persist();
        }
    }

    @Override
    public boolean exists(String id) {
        return cache.stream().anyMatch(cfg -> cfg.getId().equals(id));
    }

    // ---------------------- PRIVADOS ----------------------

    private boolean replaceById(RouteConfig newConfig) {
        for (int i = 0; i < cache.size(); i++) {
            if (Objects.equals(cache.get(i).getId(), newConfig.getId())) {
                cache.set(i, newConfig);
                return true;
            }
        }
        return false;
    }

    private List<RouteConfig> loadRoutesFromJson() {
        try {
            if (!jsonFile.exists()) {
                log.warn("Archivo de rutas no encontrado, creando archivo vacío.");
                jsonFile.getParentFile().mkdirs();
                objectMapper.writeValue(jsonFile, List.of());
            }
            return objectMapper.readValue(jsonFile, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error("Error al leer el archivo de rutas: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private void persist() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, cache);
            log.info("Archivo routes.json actualizado correctamente.");
        } catch (IOException e) {
            log.error("Error al guardar el archivo de rutas: {}", e.getMessage(), e);
        }
    }
}
