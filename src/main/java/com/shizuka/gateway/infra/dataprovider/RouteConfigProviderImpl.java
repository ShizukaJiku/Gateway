package com.shizuka.gateway.infra.dataprovider;

import com.shizuka.gateway.domain.model.RouteConfig;
import com.shizuka.gateway.domain.provider.RouteConfigProvider;
import com.shizuka.gateway.infra.repository.RouteConfigRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RouteConfigProviderImpl implements RouteConfigProvider {

  private final RouteConfigRepository routeConfigRepository;

  @Override
  public List<RouteConfig> getAll() {
    return routeConfigRepository.getAll();
  }

  @Override
  public Optional<RouteConfig> getById(String id) {
    return routeConfigRepository.getById(id);
  }

  @Override
  public boolean exists(String id) {
    return routeConfigRepository.exists(id);
  }

  @Override
  public void create(RouteConfig config) {
    routeConfigRepository.create(config);
  }

  @Override
  public void createMany(List<RouteConfig> configs) {
    routeConfigRepository.createMany(configs);
  }

  @Override
  public void update(RouteConfig config) {
    routeConfigRepository.update(config);
  }

  @Override
  public void updateMany(List<RouteConfig> configs) {
    routeConfigRepository.updateMany(configs);
  }

  @Override
  public void delete(String id) {
    routeConfigRepository.delete(id);
  }

  @Override
  public void deleteMany(List<String> ids) {
    routeConfigRepository.deleteMany(ids);
  }
}
