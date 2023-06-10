package com.example.tourism.service.imp;

import com.example.tourism.payLoad.request.UserRequest;
import com.example.tourism.service.KeyCloakService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.*;

import javax.ws.rs.core.Response;

@Service
@Slf4j
public class KeyCloakServiceImp implements KeyCloakService {
    private final Keycloak keycloak;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.realm}")
    private String realm;


    public KeyCloakServiceImp(@Qualifier("Keycloak") Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public UserRepresentation createUser(UserRequest userRequest) {

        UsersResource usersResource = keycloak.realm(realm).users();

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(userRequest.getPassword());

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(userRequest.getName());
        newUser.setEmail(userRequest.getEmail());
        newUser.setEmailVerified(false);
        newUser.setCredentials(Collections.singletonList(credentialRepresentation));
        newUser.setEnabled(true);

        try {
            Response response = usersResource.create(newUser);
            log.info("Response Status: {}", response.getStatus());

            if (response.getStatus() == 201) {
                List<UserRepresentation> search = keycloak.realm(realm).users().search(newUser.getUsername());
                newUser.setId(search.get(0).getId());
                log.info("{}", newUser.getUsername());
                addRealmRoleToUser(keycloak.realm(realm).users().search(userRequest.getName()).get(0).getId(), "user");
                log.info("Role added to user.");
                return newUser;
            }

        } catch (Exception e) {
            log.error("Exception in user creation in Keycloak: ", e);
        }
        return null;
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public void addRealmRoleToUser(String userId, String roleName) {
        String client_id = keycloak.realm(realm).clients().findByClientId(clientId).get(0).getId();
        UserResource userResource = keycloak
                .realm(realm)
                .users()
                .get(userId);

        List<RoleRepresentation> roleRepresentationList = new ArrayList<>();
        List<RoleRepresentation> roleToAdd = new LinkedList<>();
        log.info("{}, {}",client_id, realm);
        log.info("{}", keycloak.realm(realm).clients().get(client_id));

        roleToAdd.add(keycloak.realm(realm).clients().get(client_id).roles().get(roleName).toRepresentation());
        roleRepresentationList.add(keycloak.realm(realm).roles().get(roleName).toRepresentation());

        userResource.roles().clientLevel(client_id).add(roleToAdd);
        userResource.roles().realmLevel().add(roleRepresentationList);
    }
}