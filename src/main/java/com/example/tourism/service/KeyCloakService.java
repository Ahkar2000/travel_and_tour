package com.example.tourism.service;

import com.example.tourism.payLoad.request.UserRequest;
import org.keycloak.representations.idm.UserRepresentation;

import java.security.Principal;
import java.util.Map;

public interface KeyCloakService {
    UserRepresentation createUser(UserRequest userRequest, Long id);

    Map<String, Object> getUserAttribute(Principal principal);
    String getKeycloakUserID(Principal principal);
}
