package com.shizuka.gateway.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteConfig {

    private String id;                      // ID único del route
    private String name;                    // Nombre descriptivo del microservicio

    private Map<EnvEnum, EnvConfig> envConfigMap;   // Map<EnvEnum env, EnvConfig uri>
    private EnvEnum currentEnv;              // entorno activo (ej: "LOCAL")

    private String pathPattern;             // ej: "/micro1/**"
    private String rewriteRegex;            // ej: "/micro1/(?<segment>.*)"
    private String rewriteReplacement;      // ej: "/${segment}"

    private List<String> filters;           // Filtros adicionales en formato literal (AddHeader, RemoveHeader, etc.)

    public EnvConfig getCurrentEnvConfig() {
        if (!envConfigMap.containsKey(currentEnv)) {
            throw new IllegalStateException("No config found for environment: " + currentEnv);
        }

        return envConfigMap.get(currentEnv);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EnvConfig {
        private String uri;
        private Map<String, String> headers; //Cabecera, clave, valor
    }
}
