package com.shizuka.gateway.api.controller;

import com.shizuka.gateway.app.service.RouteConfigService;
import com.shizuka.gateway.domain.model.EnvEnum;
import com.shizuka.gateway.domain.model.RouteConfig;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {

  private final RouteConfigService routeConfigService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<RouteConfig> getAll() {
    return routeConfigService.list();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void create(@RequestBody RouteConfig config) {
    routeConfigService.create(config);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.OK)
  public void deleteMany(@RequestBody List<String> ids) {
    routeConfigService.deleteMany(ids);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public RouteConfig getById(@PathVariable String id) {
    return routeConfigService.findById(id);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void update(@PathVariable String id, @Valid @RequestBody RouteConfig config) {
    routeConfigService.update(config, id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable String id) {
    routeConfigService.delete(id);
  }

  @PutMapping("/env")
  @ResponseStatus(HttpStatus.OK)
  public void updateEnv(@RequestParam EnvEnum env, @RequestBody(required = false) List<String> routeIds) {
    routeConfigService.updateEnv(routeIds, env);
  }

  @PutMapping("/upsert")
  @ResponseStatus(HttpStatus.OK)
  public void upsertMany(@Valid @RequestBody List<RouteConfig> configs) {
    routeConfigService.upsertMany(configs);
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Flux<Void> uploadJson(@RequestPart FilePart file) {
    return routeConfigService.upsertFromFile(file);
  }
}
