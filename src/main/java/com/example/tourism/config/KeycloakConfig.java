package com.example.tourism.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Slf4j
public class KeycloakConfig {
    private final Environment env;

    public KeycloakConfig(Environment env) {
        this.env = env;
    }
    @Bean
    @Qualifier("Keycloak")
    public Keycloak getInstance(){
        log.info("realm: {}\nsecret:{}\nserver:{}\nusername:{}\npassword:{}",
                env.getProperty("keycloak.realm"),env.getProperty("keycloak.credentials.secret"),
                env.getProperty("keycloak.auth-server-url"),env.getProperty("kc-username"),env.getProperty("kc-password"));
        return KeycloakBuilder.builder()
                .realm(env.getProperty("keycloak.realm"))
                .serverUrl(env.getProperty("keycloak.auth-server-url"))
                .clientSecret(env.getProperty("keycloak.credentials.secret"))
                .clientId("tourism-client")
                .username(env.getProperty("kc-username"))
                .password(env.getProperty("kc-password"))
                .build();
    }
}
