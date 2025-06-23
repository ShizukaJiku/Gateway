package com.shizuka.gateway.api.controller;

import com.shizuka.gateway.app.service.RouteConfigService;
import com.shizuka.gateway.domain.model.EnvEnum;
import com.shizuka.gateway.domain.model.RouteConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteConfigService routeConfigService;

    @GetMapping
    public Flux<RouteConfig> getAll() {
        return Flux.fromIterable(routeConfigService.list());
    }

    @GetMapping("/{id}")
    public Mono<RouteConfig> getById(@PathVariable String id) {
        return Mono.just(routeConfigService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> create(@RequestBody RouteConfig config) {
        return Mono.fromRunnable(() -> routeConfigService.create(config));
    }

    @PutMapping("/{id}")
    public Mono<Void> update(@PathVariable String id, @Valid @RequestBody RouteConfig config) {
        return Mono.fromRunnable(() -> {
            config.setId(id);
            routeConfigService.update(config);
        });
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return Mono.fromRunnable(() -> routeConfigService.delete(id));
    }

    @DeleteMapping
    public Mono<Void> deleteMany(@RequestBody List<String> ids) {
        return Mono.fromRunnable(() -> routeConfigService.deleteMany(ids));
    }

    @PutMapping("/env")
    public Mono<Void> updateEnv(@RequestParam EnvEnum env, @RequestBody(required = false) List<String> routeIds) {
        return Mono.fromRunnable(() -> routeConfigService.updateEnv(routeIds, env));
    }

    @PutMapping("/upsert")
    public Mono<Void> upsertMany(@Valid @RequestBody List<RouteConfig> configs) {
        return Mono.fromRunnable(() -> routeConfigService.upsertMany(configs));
    }

    @PostMapping("/upload")
    public Mono<Void> uploadJson(@RequestParam("file") MultipartFile file) {
        return routeConfigService.upsertFromFile(file);
    }
}
