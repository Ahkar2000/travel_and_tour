package com.example.tourism.service.imp;

import com.example.tourism.entity.User;
import com.example.tourism.service.KeyCloakService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.Response;
import java.util.Collections;

@Service
@Slf4j
public class KeyCloakServiceImp implements KeyCloakService {
    private final Keycloak keycloak;
    private final Environment env;
    ObjectMapper objectMapper;

    public KeyCloakServiceImp(@Qualifier("Keycloak") Keycloak keycloak, Environment env) {
        this.keycloak = keycloak;
        this.env = env;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        this.objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    private String getRealm() {
        return env.getProperty("keycloak.realm");
    }
    private String getClientId() {
        return env.getProperty("keycloak.resource");
    }

    @Override
    public UserRepresentation createUser(User user) {

        UsersResource usersResource = keycloak.realm(getRealm()).users();

        CredentialRepresentation credentialRepresentation = createPasswordCredentials();

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setEmailVerified(false);
        newUser.setCredentials(Collections.singletonList(credentialRepresentation));

        try {
            Response response = usersResource.create(newUser);
            log.info("Response Status: {}", response.getStatus());

            if (response.getStatus() == 201) {
                List<UserRepresentation> search = keycloak.realm(getRealm()).users().search(newUser.getUsername());
                newUser.setId(search.get(0).getId());
                addRealmRoleToUser(newUser.getUsername(), newUser.getRealmRoles().get(0));
                log.info("Role added to user.");
                return newUser;
            }

        } catch (Exception e) {
            log.error("Exception in user creation in Keycloak: ", e);
        }
        return null;
    }

    private static CredentialRepresentation createPasswordCredentials() {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(true);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue("1234");
        return passwordCredentials;
    }
    public void addRealmRoleToUser(String userName, String roleName) {
        String clientId = keycloak
                .realm(getRealm())
                .clients()
                .findByClientId(getClientId())
                .get(0)
                .getId();
        String userId = keycloak
                .realm(getRealm())
                .users()
                .search(userName)
                .get(0)
                .getId();
        UserResource user = keycloak
                .realm(getRealm())
                .users()
                .get(userId);
        List<RoleRepresentation> roleToAdd = new LinkedList<>();
        roleToAdd.add(keycloak
                .realm(getRealm())
                .clients()
                .get(clientId)
                .roles()
                .get(roleName)
                .toRepresentation()
        );
        user.roles().clientLevel(clientId).add(roleToAdd);
    }
}
