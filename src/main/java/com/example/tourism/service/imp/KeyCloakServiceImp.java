package com.example.tourism.service.imp;

import com.example.tourism.payLoad.request.UserRequest;
import com.example.tourism.service.KeyCloakService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.security.Principal;
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
    public UserRepresentation createUser(UserRequest userRequest, Long id) {

        UsersResource usersResource = keycloak.realm(realm).users();

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(userRequest.getPassword());

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(userRequest.getName());
        newUser.setEmail(userRequest.getEmail());
        newUser.setEmailVerified(false);
        newUser.setCredentials(Collections.singletonList(credentialRepresentation));
        newUser.setEnabled(true);

        List<String> userIdList = new ArrayList<>();
        userIdList.add(String.valueOf(id));
        Map<String, List<String>> userId = new HashMap<>();
        userId.put("userId",userIdList);

        newUser.setAttributes(userId);
        try {
            Response response = usersResource.create(newUser);
            log.info("Response Status: {}", response.getStatus());

            if (response.getStatus() == 201) {
                List<UserRepresentation> search = keycloak.realm(realm).users().search(newUser.getUsername());
                newUser.setId(search.get(0).getId());
                log.info("{}", newUser.getUsername());
                addRoleToUser(keycloak.realm(realm).users().search(userRequest.getName()).get(0).getId(), "user");
                log.info("Role added to user.");
                return newUser;
            }

        } catch (Exception e) {
            log.error("Exception in user creation in Keycloak: ", e);
        }
        return null;
    }
    @Override
    public Map<String, Object> getUserAttribute(Principal principal) {
        try {
            return ((KeycloakPrincipal<?>) ((KeycloakAuthenticationToken) principal).getPrincipal()).getKeycloakSecurityContext()
                    .getToken().getOtherClaims();
        } catch (Exception ex) {
            log.info("When trying to get attribute", ex);
            return null;
        }
    }
    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public void addRoleToUser(String userId, String roleName) {
        String client_id = keycloak.realm(realm).clients().findByClientId(clientId).get(0).getId();
        UserResource userResource = keycloak.realm(realm).users().get(userId);

        List<RoleRepresentation> roleRepresentationList = new ArrayList<>();
        List<RoleRepresentation> roleToAdd = new LinkedList<>();
        log.info("{}, {}",client_id, realm);
        log.info("{}", keycloak.realm(realm).clients().get(client_id));

        roleToAdd.add(keycloak.realm(realm).clients().get(client_id).roles().get(roleName).toRepresentation());
        roleRepresentationList.add(keycloak.realm(realm).roles().get(roleName).toRepresentation());

        userResource.roles().clientLevel(client_id).add(roleToAdd);
        userResource.roles().realmLevel().add(roleRepresentationList);
    }

    @Override
    public String getKeycloakUserID(Principal principal){
        return (String) getUserAttribute(principal).get("userId");
    }
}