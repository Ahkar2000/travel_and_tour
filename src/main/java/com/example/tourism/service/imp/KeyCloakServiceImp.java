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

    @Override
    public UserRepresentation createUser(User user) {

        UsersResource usersResource = keycloak.realm(getRealm()).users();

        log.info("user : {}", keycloak.tokenManager().getAccessToken());
        CredentialRepresentation credentialRepresentation = createPasswordCredentials();

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setEmailVerified(false);
        newUser.setCredentials(Collections.singletonList(credentialRepresentation));

        List<String> roleList = new ArrayList<>();
        roleList.add("user");
        newUser.setRealmRoles(roleList);
        String json;
        try {
            json = objectMapper.writeValueAsString(newUser);
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert java object to json string", e);
            json = newUser.toString();
        }
        log.info("New user wanna create in keycloak: {}", json);
        try {
            Response response = usersResource.create(newUser);
            log.info("Response Status: {}", response.getStatus());

            if (response.getStatus() == 201) {
                UserRepresentation createdUser = keycloak.realm(getRealm()).users().search(newUser.getUsername()).get(0);

                UserResource userResource = usersResource.get(createdUser.getId());
                RoleMappingResource roleMappingResource = userResource.roles();

                RoleRepresentation roleRepresentation = new RoleRepresentation();
                roleRepresentation.setName("user");
                roleRepresentation.setId("user");

                roleMappingResource.realmLevel().add(Collections.singletonList(roleRepresentation));

                log.info("User created");
                return createdUser;
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
}
